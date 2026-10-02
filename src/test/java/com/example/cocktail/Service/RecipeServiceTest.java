package com.example.cocktail.Service;

import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.DTO.MaterialDTO;
import com.example.cocktail.DTO.RecipeDTO;
import com.example.cocktail.DTO.RecipeRequest;
import com.example.cocktail.Exception.BusinessException;
import com.example.cocktail.Model.BaseWine;
import com.example.cocktail.Model.Recipe;
import com.example.cocktail.Repository.CombinationRepository;
import com.example.cocktail.Repository.RecipeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 酒譜 Service 在 H2（Hibernate 產生的 schema）上的行為：刪除、全部列表、新增／編輯的重名檢查
 * Cloudinary 一律 mock，不打真的雲端
 *
 * @author Harry
 * @since 2026-10-02
 * 異動歷史：2026-10-02 Harry 新建
 * 　　　　　2026-10-02 Harry 補全部列表與重名檢查測試，取有組合的酒譜改用 filter
 * 　　　　　2026-10-02 Harry 組合改存 Recipe.combination，刪除測試改驗證有組合的酒譜數與組合本身保留
 */
@SpringBootTest
@Transactional
class RecipeServiceTest {

    private static final String NEW_EN_TITLE = "Brand New Cocktail";
    private static final String NEW_ZH_TITLE = "全新測試調酒";

    @Autowired
    private RecipeService recipeService;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private CombinationRepository combinationRepository;
    @Autowired
    private EntityManager entityManager;

    @MockitoBean
    private CloudinaryService cloudinaryService;

    @Test
    void deleteRecipeWithCombinationKeepsTheCombination() {
        // LEFT JOIN 沒有 ORDER BY，第一筆可能是沒分配組合的酒譜，先濾掉再取
        CockTailDetailDTO assigned = recipeRepository.findRecipeCombinationDetails().stream()
                .filter(detail -> detail.combinationId() != null)
                .findFirst()
                .orElseThrow();
        long recipes = recipeRepository.count();
        long assignedRecipes = countRecipesWithCombination();
        long combinations = combinationRepository.count();

        recipeService.deleteRecipe(assigned.recipeId());
        recipeRepository.flush();

        assertEquals(recipes - 1, recipeRepository.count());
        assertEquals(assignedRecipes - 1, countRecipesWithCombination());
        assertFalse(recipeRepository.existsById(assigned.recipeId()));
        // 組合是多杯酒共用的選項，刪酒譜不能連帶刪掉組合
        assertEquals(combinations, combinationRepository.count());
        assertTrue(combinationRepository.existsById(assigned.combinationId()));
    }

    @Test
    void allRecipesIncludeRecipeWithoutBaseWine() {
        Recipe noBaseWine = new Recipe();
        noBaseWine.setEnTitle(NEW_EN_TITLE);
        noBaseWine.setZhTitle(NEW_ZH_TITLE);
        noBaseWine.setMethod("沒有勾任何基酒");
        Integer noBaseWineId = recipeRepository.saveAndFlush(noBaseWine).getRecipeId();
        entityManager.clear();

        List<RecipeDTO> all = recipeService.getAllRecipes();
        List<RecipeDTO> allByBaseWine = recipeService.getRecipesByBaseWine(List.of("All"));

        assertEquals(recipeRepository.count(), all.size());
        assertTrue(all.stream().anyMatch(r -> r.recipeId().equals(noBaseWineId) && r.baseWines().isEmpty()));
        // 兩條「全部」的路徑結果要一致
        assertEquals(recipeIds(all), recipeIds(allByBaseWine));
    }

    @Test
    void addRecipeWithDuplicateTitleIsRejectedBeforeUpload() throws IOException {
        Recipe existing = anyRecipe();

        // 英文、中文名稱任一跟既有酒譜相同都算重名
        assertThrows(BusinessException.class,
                () -> recipeService.addRecipe(request(existing.getEnTitle(), NEW_ZH_TITLE), image()));
        assertThrows(BusinessException.class,
                () -> recipeService.addRecipe(request(NEW_EN_TITLE, existing.getZhTitle()), image()));

        verify(cloudinaryService, never()).uploadImage(any());
    }

    @Test
    void addRecipeWithNewTitleUploadsImageAndSaves() throws IOException {
        when(cloudinaryService.uploadImage(any())).thenReturn("https://example.com/new.jpg");
        long recipes = recipeRepository.count();

        recipeService.addRecipe(request(NEW_EN_TITLE, NEW_ZH_TITLE), image());

        verify(cloudinaryService).uploadImage(any());
        assertEquals(recipes + 1, recipeRepository.count());
        Recipe saved = findByEnTitle(NEW_EN_TITLE);
        assertEquals("https://example.com/new.jpg", saved.getImage());
        assertEquals(NEW_ZH_TITLE, saved.getZhTitle());
    }

    @Test
    void updateRecipeKeepingOwnTitleIsAllowed() throws IOException {
        Recipe existing = anyRecipe();
        Integer recipeId = existing.getRecipeId();
        RecipeRequest request = new RecipeRequest(existing.getEnTitle(), existing.getZhTitle(), "改過的作法",
                List.of("Gin"), List.of(new MaterialDTO("琴酒", "30ml")));

        recipeService.updateRecipe(recipeId, request, null);
        // 沒有呼叫 save，靠交易內 managed entity 的 dirty checking 寫回
        entityManager.flush();
        entityManager.clear();

        Recipe updated = recipeRepository.findById(recipeId).orElseThrow();
        assertEquals(existing.getEnTitle(), updated.getEnTitle());
        assertEquals("改過的作法", updated.getMethod());
        assertEquals(List.of("Gin"), updated.getBaseWines().stream().map(BaseWine::getName).toList());
        assertEquals(List.of("琴酒=30ml"), updated.getMaterials().stream()
                .map(m -> m.getMaterialName() + "=" + m.getMaterialQuantity()).toList());
        verify(cloudinaryService, never()).uploadImage(any());
    }

    @Test
    void updateRecipeToOtherRecipesTitleIsRejected() throws IOException {
        List<Recipe> recipes = recipeRepository.findAll();
        Recipe target = recipes.get(0);
        Recipe other = recipes.get(1);
        Integer targetId = target.getRecipeId();
        String originalEnTitle = target.getEnTitle();

        assertThrows(BusinessException.class, () -> recipeService.updateRecipe(
                targetId, request(other.getEnTitle(), target.getZhTitle()), image()));
        assertThrows(BusinessException.class, () -> recipeService.updateRecipe(
                targetId, request(target.getEnTitle(), other.getZhTitle()), image()));

        verify(cloudinaryService, never()).uploadImage(any());
        entityManager.clear();
        assertEquals(originalEnTitle, recipeRepository.findById(targetId).orElseThrow().getEnTitle());
    }

    /**
     * @return 有分配組合的酒譜數
     */
    private long countRecipesWithCombination() {
        return recipeRepository.findRecipeCombinationDetails().stream()
                .filter(detail -> detail.combinationId() != null)
                .count();
    }

    /**
     * @return 任一筆既有酒譜
     */
    private Recipe anyRecipe() {
        return recipeRepository.findAll().get(0);
    }

    /**
     * @param enTitle 英文名稱
     * @return 英文名稱完全相同的酒譜
     */
    private Recipe findByEnTitle(String enTitle) {
        return recipeRepository.searchByKeyword(enTitle).stream()
                .filter(r -> r.getEnTitle().equals(enTitle))
                .findFirst()
                .orElseThrow();
    }

    /**
     * @param enTitle 英文名稱
     * @param zhTitle 中文名稱
     * @return 只有名稱不同、其餘固定的請求內容
     */
    private static RecipeRequest request(String enTitle, String zhTitle) {
        return new RecipeRequest(enTitle, zhTitle, "測試作法",
                List.of("Vodka"), List.of(new MaterialDTO("伏特加", "45ml")));
    }

    /**
     * @return 假的上傳圖片
     */
    private static MockMultipartFile image() {
        return new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[]{1, 2, 3});
    }

    /**
     * @param recipes 酒譜列表
     * @return 酒譜 ID 集合
     */
    private static Set<Integer> recipeIds(List<RecipeDTO> recipes) {
        return recipes.stream().map(RecipeDTO::recipeId).collect(Collectors.toSet());
    }
}
