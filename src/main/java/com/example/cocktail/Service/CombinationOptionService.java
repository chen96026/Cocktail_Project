package com.example.cocktail.Service;

import com.example.cocktail.DTO.AssignmentRequest;
import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.Exception.BusinessException;
import com.example.cocktail.Exception.NotFoundException;
import com.example.cocktail.Model.CombinationOption;
import com.example.cocktail.Model.Combinations;
import com.example.cocktail.Model.Recipe;
import com.example.cocktail.Repository.CombinationOptionRepository;
import com.example.cocktail.Repository.CombinationRepository;
import com.example.cocktail.Repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public List<CockTailDetailDTO> getAllRecipeCombinations() {
        return combinationOptionRepository.findRecipeCombinationDetails();
    }

    /**
     * 重新分配調酒與組合，先清掉舊的再建立新的
     * 整批放在同一個交易，避免中途失敗留下「舊的刪了、新的沒進去」的狀態
     * 任一筆缺 ID 就整批拒絕，不做任何異動
     *
     * @param assignments 要建立的調酒與組合關聯
     */
    @Transactional
    public void assignCombinations(List<AssignmentRequest> assignments) {
        if (assignments.stream().anyMatch(CombinationOptionService::isIncomplete)) {
            throw new BusinessException("分配資料缺少酒譜或組合 ID");
        }
        for (AssignmentRequest assignment : assignments) {
            assignCombination(assignment.recipeId(), assignment.combinationId());
        }
    }

    /**
     * @param recipeId      酒譜 ID
     * @param combinationId 組合 ID
     */
    private void assignCombination(Integer recipeId, Integer combinationId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("找不到該酒譜: " + recipeId));
        Combinations combination = combinationRepository.findById(combinationId)
                .orElseThrow(() -> new NotFoundException("找不到該組合: " + combinationId));

        combinationOptionRepository.deleteByFkRecipeId(recipe);

        CombinationOption combinationOption = new CombinationOption();
        combinationOption.setFkRecipeId(recipe);
        combinationOption.setFkCombinationId(combination);
        combinationOptionRepository.save(combinationOption);
    }

    /**
     * @param assignment 單筆分配
     * @return 整筆是 null，或酒譜、組合 ID 任一為 null
     */
    private static boolean isIncomplete(AssignmentRequest assignment) {
        return assignment == null || assignment.recipeId() == null || assignment.combinationId() == null;
    }
}
