package com.example.cocktail.DTO;

/**
 * 篩選器結果用的調酒基本資料
 */
public record CocktailBasicDTO(Integer recipeId, String zhTitle, String enTitle, String image) {
}
