package com.example.cocktail.Service;

import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.Exception.NotFoundException;
import com.example.cocktail.Model.CombinationOption;
import com.example.cocktail.Model.Combinations;
import com.example.cocktail.Model.Recipe;
import com.example.cocktail.Repository.CombinationOptionRepository;
import com.example.cocktail.Repository.CombinationRepository;
import com.example.cocktail.Repository.RecipeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CombinationOptionService {

    private final CombinationOptionRepository combinationOptionRepository;
    private final RecipeRepository recipeRepository;
    private final CombinationRepository combinationRepository;

    public CombinationOptionService(CombinationOptionRepository combinationOptionRepository,
                                    RecipeRepository recipeRepository,
                                    CombinationRepository combinationRepository) {
        this.combinationOptionRepository = combinationOptionRepository;
        this.recipeRepository = recipeRepository;
        this.combinationRepository = combinationRepository;
    }

    /**
     * @return 所有調酒與其組合的對應
     */
    public List<CockTailDetailDTO> getAllRecipeCombinations() {
        return combinationOptionRepository.findRecipeCombinationDetails();
    }

    /**
     * 重新分配調酒與組合，先清掉舊的再建立新的
     * 整批放在同一個交易，避免中途失敗留下「舊的刪了、新的沒進去」的狀態
     *
     * @param combinationOptions 要建立的調酒與組合關聯
     */
    @Transactional
    public void assignCombinations(List<CombinationOption> combinationOptions) {
        for (CombinationOption combinationOption : combinationOptions) {
            removeCombination(combinationOption.getFkRecipeId().getRecipeId());
            assignCombination(combinationOption);
        }
    }

    private void assignCombination(CombinationOption combinationOption) {
        Integer recipeId = combinationOption.getFkRecipeId().getRecipeId();
        Integer combinationId = combinationOption.getFkCombinationId().getCombinationId();

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("找不到該酒譜: " + recipeId));
        Combinations combination = combinationRepository.findById(combinationId)
                .orElseThrow(() -> new NotFoundException("找不到該組合: " + combinationId));

        combinationOption.setFkRecipeId(recipe);
        combinationOption.setFkCombinationId(combination);

        combinationOptionRepository.save(combinationOption);
    }

    private void removeCombination(Integer recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("找不到該酒譜: " + recipeId));
        combinationOptionRepository.deleteByFkRecipeId(recipe);
    }
}
