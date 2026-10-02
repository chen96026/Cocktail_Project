package com.example.cocktail.Seed;

import com.example.cocktail.Model.BaseWine;
import com.example.cocktail.Model.Recipe;
import com.example.cocktail.Repository.BaseWineRepository;
import com.example.cocktail.Repository.CombinationOptionRepository;
import com.example.cocktail.Repository.CombinationRepository;
import com.example.cocktail.Repository.MaterialRepository;
import com.example.cocktail.Repository.RecipeRepository;
import com.example.cocktail.Seed.SeedProperties.RecipeSeed;
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

/**
 * 驗證 DataSeeder 把 seed-data.yaml 完整載入 H2
 * 筆數一律跟 YAML 比，之後增刪酒譜不用改測試
 *
 * @author Harry
 * @since 2026-10-02
 * 異動歷史：2026-10-02 Harry 新建
 * 　　　　　2026-10-02 Harry 取酒譜改比對完整名稱，基酒改用 Set 比較
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
    @Autowired
    private CombinationOptionRepository combinationOptionRepository;

    @Test
    void seedsEveryRowFromYaml() {
        List<RecipeSeed> seeds = seedProperties.recipes();
        assertFalse(seeds.isEmpty());

        assertEquals(seeds.size(), recipeRepository.count());
        assertEquals(seeds.stream().mapToLong(s -> s.materials().size()).sum(), materialRepository.count());
        assertEquals(seeds.stream().flatMap(s -> s.baseWines().stream()).distinct().count(), baseWineRepository.count());
        assertEquals(seeds.stream().map(RecipeSeed::combination).filter(Objects::nonNull).distinct().count(),
                combinationRepository.count());
        assertEquals(seeds.stream().filter(s -> s.combination() != null).count(), combinationOptionRepository.count());
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
        assertEquals(seed.materials().stream().map(m -> m.name() + "=" + m.quantity()).toList(),
                recipe.getMaterials().stream().map(m -> m.getMaterialName() + "=" + m.getMaterialQuantity()).toList());
    }

    @Test
    void baseWineFilterQueryRunsOnH2() {
        long expected = seedProperties.recipes().stream()
                .filter(s -> s.baseWines().containsAll(List.of("Vodka", "Others")))
                .count();

        assertEquals(expected, recipeRepository.findByMatchingBaseWines(List.of("Vodka", "Others"), 2).size());
    }
}
