package com.example.cocktail.service;

import com.example.cocktail.dto.MaterialDTO;
import com.example.cocktail.dto.RecipeDTO;
import com.example.cocktail.dto.RecipeRequest;
import com.example.cocktail.exception.BusinessException;
import com.example.cocktail.exception.NotFoundException;
import com.example.cocktail.model.BaseWine;
import com.example.cocktail.model.Material;
import com.example.cocktail.model.Recipe;
import com.example.cocktail.repository.BaseWineRepository;
import com.example.cocktail.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    // 前端基酒篩選的「全部」選項
    private static final String ALL_BASE_WINES = "All";

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
     * 酒譜列表，基酒與關鍵字都是選填，兩個都帶時取交集
     * 基酒：酒譜要包含所有指定的基酒；不帶、只有空白或含 All 表示不篩基酒
     * 關鍵字：中英文名稱模糊比對、不分大小寫；不帶或只有空白表示不篩關鍵字
     * 材料、基酒靠 hibernate.default_batch_fetch_size 批次載入，不會逐筆查詢
     *
     * @param baseWines 基酒名稱清單，元素可再以逗號分隔
     * @param keyword   中英文名稱關鍵字
     * @return 符合條件的酒譜（都不篩時含沒有勾基酒的）
     */
    @Transactional(readOnly = true)
    public List<RecipeDTO> findRecipes(List<String> baseWines, String keyword) {
        return queryRecipes(toBaseWineFilter(baseWines), keyword == null ? "" : keyword.strip()).stream()
                .map(RecipeDTO::from)
                .toList();
    }

    /**
     * 先檢查重名再上傳圖片，避免重名失敗時 Cloudinary 留下沒人用的圖
     *
     * @param request 酒譜內容
     * @param image   酒譜圖片
     * @return 新酒譜的 ID
     */
    @Transactional
    public Integer addRecipe(RecipeRequest request, MultipartFile image) {
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

        return recipeRepository.save(recipe).getRecipeId();
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
     * 依有帶的條件選查詢，都不帶時跟 All 一樣走 findAll，兩條「全部」的結果一致
     *
     * @param baseWines 已整理過的基酒名稱，空清單表示不篩基酒
     * @param keyword   已去頭尾空白的關鍵字，空字串表示不篩關鍵字
     * @return 符合條件的酒譜 Entity
     */
    private List<Recipe> queryRecipes(List<String> baseWines, String keyword) {
        boolean byBaseWine = !baseWines.isEmpty();
        boolean byKeyword = !keyword.isEmpty();
        if (byBaseWine && byKeyword) {
            return recipeRepository.findByMatchingBaseWinesAndKeyword(baseWines, baseWines.size(), keyword);
        }
        if (byBaseWine) {
            return recipeRepository.findByMatchingBaseWines(baseWines, baseWines.size());
        }
        if (byKeyword) {
            return recipeRepository.searchByKeyword(keyword);
        }
        return recipeRepository.findAll();
    }

    /**
     * 查詢用 COUNT(DISTINCT) 比對數量，重複的名稱要先去掉，否則永遠湊不到數
     *
     * @param baseWines 前端帶來的基酒名稱，可能是逗號分隔、重複或空白
     * @return 去重後的基酒名稱；未指定或含 All 時為空清單
     */
    private static List<String> toBaseWineFilter(List<String> baseWines) {
        if (baseWines == null) {
            return List.of();
        }
        List<String> names = baseWines.stream()
                .flatMap(value -> Arrays.stream(value.split(",")))
                .map(String::strip)
                .filter(name -> !name.isEmpty())
                .distinct()
                .toList();
        return names.contains(ALL_BASE_WINES) ? List.of() : names;
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
