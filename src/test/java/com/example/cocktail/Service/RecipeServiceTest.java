package com.example.cocktail.Service;

import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.Repository.CombinationOptionRepository;
import com.example.cocktail.Repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * 酒譜刪除在 H2（Hibernate 產生的 schema）上的行為
 *
 * @author Harry
 * @since 2026-10-02
 * 異動歷史：2026-10-02 Harry 新建
 * 　　　　　2026-10-02 Harry 取有組合的酒譜改用 filter
 */
@SpringBootTest
@Transactional
class RecipeServiceTest {

    @Autowired
    private RecipeService recipeService;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private CombinationOptionRepository combinationOptionRepository;

    @Test
    void deleteRecipeAlsoRemovesItsCombinationOption() {
        // LEFT JOIN 沒有 ORDER BY，第一筆可能是沒分配組合的酒譜，先濾掉再取
        CockTailDetailDTO assigned = combinationOptionRepository.findRecipeCombinationDetails().stream()
                .filter(detail -> detail.combinationId() != null)
                .findFirst()
                .orElseThrow();
        long recipes = recipeRepository.count();
        long options = combinationOptionRepository.count();

        recipeService.deleteRecipe(assigned.recipeId());
        recipeRepository.flush();

        assertEquals(recipes - 1, recipeRepository.count());
        assertEquals(options - 1, combinationOptionRepository.count());
        assertFalse(recipeRepository.existsById(assigned.recipeId()));
    }
}
