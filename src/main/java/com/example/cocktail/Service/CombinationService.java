package com.example.cocktail.Service;

import com.example.cocktail.DTO.CocktailBasicDTO;
import com.example.cocktail.DTO.CocktailSelectorDTO;
import com.example.cocktail.Model.Combinations;

import java.util.List;

public interface CombinationService {
    public void addCombination(Combinations combination);
    public boolean isCombinationExists(String mood, String taste, String tone, String drunk);

}
