package com.example.cocktail.Service;

import com.example.cocktail.Model.Combinations;

public interface CombinationService {
    public void addCombination(Combinations combination);
    public boolean isCombinationExists(String mood, String taste, String tone, String drunk);

}
