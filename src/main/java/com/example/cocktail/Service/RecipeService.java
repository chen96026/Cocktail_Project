package com.example.cocktail.Service;

import com.example.cocktail.DTO.MaterialDTO;
import com.example.cocktail.DTO.RecipeDTO;
import com.example.cocktail.DTO.RecipeRequest;
import com.example.cocktail.Exception.BusinessException;
import com.example.cocktail.Exception.NotFoundException;
import com.example.cocktail.Model.BaseWine;
import com.example.cocktail.Model.Material;
import com.example.cocktail.Model.Recipe;
import com.example.cocktail.Repository.BaseWineRepository;
import com.example.cocktail.Repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final BaseWineRepository baseWineRepository;
    private final CloudinaryService cloudinaryService;

    public RecipeService(RecipeRepository recipeRepository,
                         BaseWineRepository baseWineRepository,
                         CloudinaryService cloudinaryService) {
        this.recipeRepository = recipeRepository;
        this.baseWineRepository = baseWineRepository;
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * 材料、基酒靠 hibernate.default_batch_fetch_size 批次載入，不會逐筆查詢
     *
     * @return 所有酒譜（含沒有勾基酒的）
     */
    @Transactional(readOnly = true)
    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(RecipeDTO::from)
                .toList();
    }

    /**
     * @param baseWineList 基酒名稱清單，含 All 時回傳全部
     * @return 符合基酒條件的酒譜
     */
    @Transactional(readOnly = true)
    public List<RecipeDTO> getRecipesByBaseWine(List<String> baseWineList) {
        // All 跟 getAllRecipe 走同一條路徑，兩邊的「全部」結果一致
        if (baseWineList.contains("All")) {
            return getAllRecipes();
        }
        return recipeRepository.findByMatchingBaseWines(baseWineList, baseWineList.size()).stream()
                .map(RecipeDTO::from)
                .toList();
    }

    /**
     * 先檢查重名再上傳圖片，避免重名失敗時 Cloudinary 留下沒人用的圖
     *
     * @param request 酒譜內容
     * @param image   酒譜圖片
     */
    @Transactional
    public void addRecipe(RecipeRequest request, MultipartFile image) {
        if (recipeRepository.existsByEnTitleOrZhTitle(request.enTitle(), request.zhTitle())) {
            throw new BusinessException("酒譜名稱已存在，無法重複新增");
        }

        Recipe recipe = new Recipe();
        recipe.setEnTitle(request.enTitle());
        recipe.setZhTitle(request.zhTitle());
        recipe.setMethod(request.method());
        recipe.setImage(uploadImage(image));
        recipe.setBaseWines(resolveBaseWines(request.baseWines()));
        recipe.setMaterials(toMaterials(request.materials(), recipe));

        recipeRepository.save(recipe);
    }

    /**
     * 名稱沿用自己原本的不算重名，跟別杯相同才擋下
     * existingRecipe 是 managed entity，交易結束時自動 flush，不用再呼叫 save
     *
     * @param recipeId 酒譜 ID
     * @param request  酒譜內容
     * @param image    新圖片，未帶則保留原圖
     */
    @Transactional
    public void updateRecipe(Integer recipeId, RecipeRequest request, MultipartFile image) {
        Recipe existingRecipe = findRecipe(recipeId);
        // 要在改 Entity 欄位之前檢查，否則查詢前的 auto flush 會先把重名寫進 DB 撞 unique
        if (recipeRepository.existsTitleInOtherRecipe(request.enTitle(), request.zhTitle(), recipeId)) {
            throw new BusinessException("酒譜名稱與其他酒譜重複，無法更新");
        }

        existingRecipe.setEnTitle(request.enTitle());
        existingRecipe.setZhTitle(request.zhTitle());
        existingRecipe.setMethod(request.method());
        // 有帶新圖片才更新，否則保留原圖
        if (image != null && !image.isEmpty()) {
            existingRecipe.setImage(uploadImage(image));
        }
        existingRecipe.setBaseWines(resolveBaseWines(request.baseWines()));

        // 清空舊的材料並設置新的材料
        existingRecipe.getMaterials().clear();
        existingRecipe.getMaterials().addAll(toMaterials(request.materials(), existingRecipe));
    }

    /**
     * @param recipeId 酒譜 ID
     */
    @Transactional
    public void deleteRecipe(Integer recipeId) {
        recipeRepository.delete(findRecipe(recipeId));
    }

    /**
     * 在交易內轉成 DTO，回傳形狀跟 getAllRecipes 一致
     *
     * @param recipeId 酒譜 ID
     * @return 對應的酒譜
     */
    @Transactional(readOnly = true)
    public RecipeDTO getRecipe(Integer recipeId) {
        return RecipeDTO.from(findRecipe(recipeId));
    }

    /**
     * @param keyword 中英文名稱關鍵字
     * @return 符合關鍵字的酒譜
     */
    @Transactional(readOnly = true)
    public List<RecipeDTO> searchRecipes(String keyword) {
        return recipeRepository.searchByKeyword(keyword).stream()
                .map(RecipeDTO::from)
                .toList();
    }

    /**
     * 只給 Service 內部用，Entity 不離開交易
     *
     * @param recipeId 酒譜 ID
     * @return 對應的酒譜 Entity
     */
    private Recipe findRecipe(Integer recipeId) {
        Recipe recipe = recipeRepository.findByRecipeId(recipeId);
        if (recipe == null) {
            throw new NotFoundException("找不到該酒譜，ID: " + recipeId);
        }
        return recipe;
    }

    /**
     * @param image 要上傳的圖片
     * @return 上傳後的圖片 URL
     */
    private String uploadImage(MultipartFile image) {
        try {
            return cloudinaryService.uploadImage(image);
        } catch (IOException e) {
            throw new RuntimeException("圖片上傳失敗", e);
        }
    }

    /**
     * 依名稱取得基酒，資料庫沒有的自動新建
     * 跟著呼叫端的交易，酒譜存檔失敗時新建的基酒一起回滾
     *
     * @param names 基酒名稱清單
     * @return 對應的基酒 Entity 清單
     */
    private List<BaseWine> resolveBaseWines(List<String> names) {
        return names.stream()
                .map(name -> baseWineRepository.findByName(name)
                        .orElseGet(() -> {
                            BaseWine newWine = new BaseWine();
                            newWine.setName(name);
                            return baseWineRepository.save(newWine);
                        }))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @param materials 材料清單
     * @param recipe    所屬酒譜
     * @return 對應的材料 Entity 清單
     */
    private List<Material> toMaterials(List<MaterialDTO> materials, Recipe recipe) {
        return materials.stream()
                .map(dto -> {
                    Material material = new Material();
                    material.setMaterialName(dto.materialName());
                    material.setMaterialQuantity(dto.materialQuantity());
                    material.setRecipe(recipe);
                    return material;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
