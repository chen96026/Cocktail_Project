package com.example.cocktail.Service;

import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.Model.CombinationOption;
import com.example.cocktail.Model.Combinations;
import com.example.cocktail.Model.Recipe;
import com.example.cocktail.Repository.CombinationOptionRepository;
import com.example.cocktail.Repository.CombinationRepository;
import com.example.cocktail.Repository.RecipeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CombinationOptionServiceImpl implements CombinationOptionService {

    @Autowired
    private CombinationOptionRepository combinationOptionRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private CombinationRepository combinationRepository;


    @Override
    public List<CockTailDetailDTO> getAllRecipeCombinations() {
        return combinationOptionRepository.findRecipeCombinationDetails();
    }

    @Override
    @Transactional
    public void assignCombination(CombinationOption combinationOption) {
        Recipe recipe = recipeRepository.findById(combinationOption.getFkRecipeId().getRecipe_id())
                .orElseThrow(() -> new IllegalArgumentException("找不到該酒譜: " + combinationOption.getFkRecipeId().getRecipe_id()));

        Combinations combination = combinationRepository.findById(combinationOption.getFkCombinationId().getCombinationId())
                .orElseThrow(() -> new IllegalArgumentException("找不到該組合: " + combinationOption.getFkCombinationId().getCombinationId()));

        combinationOption.setFkRecipeId(recipe);
        combinationOption.setFkCombinationId(combination);

        combinationOptionRepository.save(combinationOption);
    }

    @Override
    @Transactional
    public void removeCombination(Integer recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("找不到該酒譜: " + recipeId));
        combinationOptionRepository.deleteByFkRecipeId(recipe);
    }
}
