package com.example.cocktail.service;

import com.example.cocktail.dto.AssignmentRequest;
import com.example.cocktail.dto.CocktailDetailDTO;
import com.example.cocktail.exception.BusinessException;
import com.example.cocktail.exception.NotFoundException;
import com.example.cocktail.model.Combination;
import com.example.cocktail.model.Recipe;
import com.example.cocktail.repository.CombinationRepository;
import com.example.cocktail.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecipeCombinationService {

    private final RecipeRepository recipeRepository;
    private final CombinationRepository combinationRepository;

    public RecipeCombinationService(RecipeRepository recipeRepository,
                                    CombinationRepository combinationRepository) {
        this.recipeRepository = recipeRepository;
        this.combinationRepository = combinationRepository;
    }

    /**
     * @return 所有調酒與其組合的對應
     */
    @Transactional(readOnly = true)
    public List<CocktailDetailDTO> getAllRecipeCombinations() {
        return recipeRepository.findRecipeCombinationDetails();
    }

    /**
     * 重新分配調酒與組合，組合存在 Recipe.combination，新的直接取代原本的（一杯酒最多一組）
     * 整批放在同一個交易，任一筆失敗整批回滾
     * 任一筆缺 ID 就整批拒絕，不做任何異動
     *
     * @param assignments 要建立的調酒與組合關聯
     */
    @Transactional
    public void assignCombinations(List<AssignmentRequest> assignments) {
        if (assignments.stream().anyMatch(RecipeCombinationService::isIncomplete)) {
            throw new BusinessException("分配資料缺少酒譜或組合 ID");
        }
        for (AssignmentRequest assignment : assignments) {
            assignCombination(assignment.recipeId(), assignment.combinationId());
        }
    }

    /**
     * recipe 是 managed entity，交易結束時自動 flush，不用再呼叫 save
     *
     * @param recipeId      酒譜 ID
     * @param combinationId 組合 ID
     */
    private void assignCombination(Integer recipeId, Integer combinationId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("找不到該酒譜: " + recipeId));
        Combination combination = combinationRepository.findById(combinationId)
                .orElseThrow(() -> new NotFoundException("找不到該組合: " + combinationId));

        recipe.setCombination(combination);
    }

    /**
     * @param assignment 單筆分配
     * @return 整筆是 null，或酒譜、組合 ID 任一為 null
     */
    private static boolean isIncomplete(AssignmentRequest assignment) {
        return assignment == null || assignment.recipeId() == null || assignment.combinationId() == null;
    }
}
