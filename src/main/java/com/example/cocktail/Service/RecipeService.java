package com.example.cocktail.Service;

import com.example.cocktail.DTO.MaterialDTO;
import com.example.cocktail.DTO.RecipeDTO;
import com.example.cocktail.DTO.RecipeRequest;
import com.example.cocktail.Exception.NotFoundException;
import com.example.cocktail.Model.BaseWine;
import com.example.cocktail.Model.Material;
import com.example.cocktail.Model.Recipe;
import com.example.cocktail.Repository.BaseWineRepository;
import com.example.cocktail.Repository.RecipeRepository;
import org.springframework.stereotype.Service;
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
     * @return 所有酒譜（含基酒）
     */
    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findAllWithBaseWines().stream()
                .map(RecipeDTO::from)
                .toList();
    }

    /**
     * @param baseWineList 基酒名稱清單，含 All 時回傳全部
     * @return 符合基酒條件的酒譜
     */
    public List<RecipeDTO> getRecipesByBaseWine(List<String> baseWineList) {
        List<Recipe> recipes = baseWineList.contains("All")
                ? recipeRepository.findAll()
                : recipeRepository.findByMatchingBaseWines(baseWineList, baseWineList.size());
        return recipes.stream().map(RecipeDTO::from).toList();
    }

    /**
     * @param request 酒譜內容
     * @param image   酒譜圖片
     */
    public void addRecipe(RecipeRequest request, MultipartFile image) {
        Recipe recipe = new Recipe();
        recipe.setEn_title(request.enTitle());
        recipe.setZh_title(request.zhTitle());
        recipe.setMethod(request.method());
        recipe.setImage(uploadImage(image));
        recipe.setBaseWines(resolveBaseWines(request.baseWines()));
        recipe.setMaterials(toMaterials(request.materials(), recipe));

        recipeRepository.save(recipe);
    }

    /**
     * @param recipeId 酒譜 ID
     * @param request  酒譜內容
     * @param image    新圖片，未帶則保留原圖
     */
    public void updateRecipe(Integer recipeId, RecipeRequest request, MultipartFile image) {
        Recipe existingRecipe = getRecipe(recipeId);

        existingRecipe.setEn_title(request.enTitle());
        existingRecipe.setZh_title(request.zhTitle());
        existingRecipe.setMethod(request.method());
        // 有帶新圖片才更新，否則保留原圖
        if (image != null && !image.isEmpty()) {
            existingRecipe.setImage(uploadImage(image));
        }
        existingRecipe.setBaseWines(resolveBaseWines(request.baseWines()));

        // 清空舊的材料並設置新的材料
        existingRecipe.getMaterials().clear();
        existingRecipe.getMaterials().addAll(toMaterials(request.materials(), existingRecipe));

        recipeRepository.save(existingRecipe);
    }

    /**
     * @param recipe_id 酒譜 ID
     */
    public void deleteRecipe(Integer recipe_id) {
        recipeRepository.delete(getRecipe(recipe_id));
    }

    /**
     * @param recipe_id 酒譜 ID
     * @return 對應的酒譜 Entity
     */
    public Recipe getRecipe(Integer recipe_id) {
        Recipe recipe = recipeRepository.findByRecipeId(recipe_id);
        if (recipe == null) {
            throw new NotFoundException("找不到該酒譜，ID: " + recipe_id);
        }
        return recipe;
    }

    /**
     * @param keyword 中英文名稱關鍵字
     * @return 符合關鍵字的酒譜
     */
    public List<Recipe> searchRecipes(String keyword) {
        return recipeRepository.searchByKeyword(keyword);
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
                    material.setMaterial_name(dto.material_name());
                    material.setMaterial_quantity(dto.material_quantity());
                    material.setRecipe(recipe);
                    return material;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
