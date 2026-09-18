package com.example.cocktail.DTO;

import com.example.cocktail.Model.BaseWine;
import com.example.cocktail.Model.Recipe;

import java.util.List;

/**
 * 酒譜列表／查詢用的回傳資料，欄位名沿用既有 API 契約
 */
public record RecipeDTO(Integer recipeId, String enTitle, String zhTitle, String method,
                        String image, List<String> baseWines, List<MaterialDTO> materials) {

    /**
     * @param recipe 來源 Entity
     * @return 對應的 DTO
     */
    public static RecipeDTO from(Recipe recipe) {
        return new RecipeDTO(
                recipe.getRecipeId(),
                recipe.getEnTitle(),
                recipe.getZhTitle(),
                recipe.getMethod(),
                recipe.getImage(),
                recipe.getBaseWines().stream().map(BaseWine::getName).toList(),
                recipe.getMaterials().stream()
                        .map(m -> new MaterialDTO(m.getMaterialName(), m.getMaterialQuantity()))
                        .toList()
        );
    }
}
