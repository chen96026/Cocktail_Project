package com.example.cocktail.seed;

import com.example.cocktail.model.BaseWine;
import com.example.cocktail.model.Combination;
import com.example.cocktail.model.Recipe;
import com.example.cocktail.repository.BaseWineRepository;
import com.example.cocktail.repository.CombinationRepository;
import com.example.cocktail.repository.MaterialRepository;
import com.example.cocktail.repository.RecipeRepository;
import com.example.cocktail.seed.SeedProperties.CombinationSeed;
import com.example.cocktail.seed.SeedProperties.RecipeSeed;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 驗證 DataSeeder 把 seed-data.yaml 完整載入 H2
 * 筆數一律跟 YAML 比，之後增刪酒譜不用改測試
 *
 * @author Harry
 * @since 2026-10-02
 * 異動歷史：2026-10-02 Harry 新建
 * 　　　　　2026-10-02 Harry 取酒譜改比對完整名稱，基酒改用 Set 比較
 * 　　　　　2026-10-02 Harry 組合改由 Recipe.combination 驗證，材料順序改靠 @OrderBy 保證
 * 　　　　　2026-10-02 Harry 組合 Entity 更名為 Combination
 */
@SpringBootTest
@Transactional
class DataSeederTest {

    @Autowired
    private SeedProperties seedProperties;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private BaseWineRepository baseWineRepository;
    @Autowired
    private CombinationRepository combinationRepository;

    @Test
    void seedsEveryRowFromYaml() {
        List<RecipeSeed> seeds = seedProperties.recipes();
        assertFalse(seeds.isEmpty());

        assertEquals(seeds.size(), recipeRepository.count());
        assertEquals(seeds.stream().mapToLong(s -> s.materials().size()).sum(), materialRepository.count());
        assertEquals(seeds.stream().flatMap(s -> s.baseWines().stream()).distinct().count(), baseWineRepository.count());
        assertEquals(seeds.stream().map(RecipeSeed::combination).filter(Objects::nonNull).distinct().count(),
                combinationRepository.count());
        // 每杯有組合的種子酒譜，對應的 Recipe 都要帶到組合
        assertEquals(seeds.stream().filter(s -> s.combination() != null).count(),
                recipeRepository.findAll().stream().filter(r -> r.getCombination() != null).count());
    }

    @Test
    void seedsRecipeContent() {
        RecipeSeed seed = seedProperties.recipes().get(0);
        // searchByKeyword 是 LIKE 模糊查詢，可能撈到名稱包含它的別杯，取名稱完全相同的那筆
        Recipe recipe = recipeRepository.searchByKeyword(seed.enTitle()).stream()
                .filter(r -> r.getEnTitle().equals(seed.enTitle()))
                .findFirst()
                .orElseThrow();

        assertEquals(seed.zhTitle(), recipe.getZhTitle());
        assertEquals(seed.image(), recipe.getImage());
        assertEquals(seed.method(), recipe.getMethod());
        // 基酒是 ManyToMany 且沒有 @OrderColumn，載入順序不保證，用 Set 比較
        assertEquals(Set.copyOf(seed.baseWines()),
                recipe.getBaseWines().stream().map(BaseWine::getName).collect(Collectors.toSet()));
        // 材料有 @OrderBy("materialId")，載入順序就是 YAML 裡的順序
        assertEquals(seed.materials().stream().map(m -> m.name() + "=" + m.quantity()).toList(),
                recipe.getMaterials().stream().map(m -> m.getMaterialName() + "=" + m.getMaterialQuantity()).toList());
        assertCombination(seed.combination(), recipe.getCombination());
    }

    @Test
    void baseWineFilterQueryRunsOnH2() {
        long expected = seedProperties.recipes().stream()
                .filter(s -> s.baseWines().containsAll(List.of("Vodka", "Others")))
                .count();

        assertEquals(expected, recipeRepository.findByMatchingBaseWines(List.of("Vodka", "Others"), 2).size());
    }

    /**
     * @param expected 種子酒譜的組合，沒有分配時為 null
     * @param actual   酒譜實際帶到的組合
     */
    private static void assertCombination(CombinationSeed expected, Combination actual) {
        if (expected == null) {
            assertNull(actual);
            return;
        }
        assertEquals(expected, new CombinationSeed(actual.getMood(), actual.getTaste(), actual.getTone(), actual.getDrunk()));
    }
}
