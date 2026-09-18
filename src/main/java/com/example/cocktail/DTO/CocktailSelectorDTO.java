package com.example.cocktail.DTO;

/**
 * 調酒篩選器的四個條件
 */
public record CocktailSelectorDTO(String mood, String taste, String tone, String drunk) {
}
