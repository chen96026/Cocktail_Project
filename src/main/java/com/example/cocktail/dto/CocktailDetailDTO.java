package com.example.cocktail.dto;

import java.util.List;

/**
 * 調酒詳細資料（含四維度組合）
 */
public record CocktailDetailDTO(Integer recipeId, String image, String enTitle, String zhTitle,
                                List<MaterialDTO> materials, String method, Integer combinationId,
                                String mood, String taste, String tone, String drunk) {

    /**
     * JPQL 投影用的建構子，材料需另外查詢後以 {@link #withMaterials} 補上
     */
    public CocktailDetailDTO(Integer recipeId, String image, String enTitle, String zhTitle, String method,
                             Integer combinationId, String mood, String taste, String tone, String drunk) {
        this(recipeId, image, enTitle, zhTitle, null, method, combinationId, mood, taste, tone, drunk);
    }

    /**
     * @param materials 要帶入的材料清單
     * @return 帶有材料清單的新實例
     */
    public CocktailDetailDTO withMaterials(List<MaterialDTO> materials) {
        return new CocktailDetailDTO(recipeId, image, enTitle, zhTitle, materials, method,
                combinationId, mood, taste, tone, drunk);
    }
}
