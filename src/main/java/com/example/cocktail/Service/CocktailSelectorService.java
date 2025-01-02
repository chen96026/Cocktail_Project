package com.example.cocktail.Service;

import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.DTO.CocktailBasicDTO;
import com.example.cocktail.DTO.CocktailSelectorDTO;

import java.util.List;

public interface CocktailSelectorService {
    // 調酒篩選器
    public List<CocktailBasicDTO> findRecipesByCombination(CocktailSelectorDTO CocktailSelectorDTO);

    // 調酒篩選器的Modal
    public CockTailDetailDTO getCocktailDetail(Integer recipeId);
}
