package com.example.cocktail.Service;

import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.Model.CombinationOption;

import java.util.List;

public interface CombinationOptionService {

    // 查詢所有調酒與組合
    public List<CockTailDetailDTO> getAllRecipeCombinations();

    // 分配組合
    public void assignCombination(CombinationOption combinationOption);

    // 移除組合分配
    public void removeCombination(Integer recipeId);

}
