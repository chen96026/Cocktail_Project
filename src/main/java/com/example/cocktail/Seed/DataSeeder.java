package com.example.cocktail.Seed;

import com.example.cocktail.Model.BaseWine;
import com.example.cocktail.Model.Combinations;
import com.example.cocktail.Model.Material;
import com.example.cocktail.Model.Recipe;
import com.example.cocktail.Repository.BaseWineRepository;
import com.example.cocktail.Repository.CombinationRepository;
import com.example.cocktail.Repository.RecipeRepository;
import com.example.cocktail.Seed.SeedProperties.CombinationSeed;
import com.example.cocktail.Seed.SeedProperties.RecipeSeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 啟動時把 seed-data.yaml 的調酒資料載入資料庫
 * mysql profile 不啟用，那時資料以 MySQL 既有內容為準
 *
 * @author Harry
 * @since 2026-10-02
 * 異動歷史：2026-10-02 Harry 新建
 * 　　　　　2026-10-02 Harry 組合改為直接設定 Recipe.combination，不再寫中介表
 */
@Component
@Profile("!mysql")
@EnableConfigurationProperties(SeedProperties.class)
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final SeedProperties seedProperties;
    private final RecipeRepository recipeRepository;
    private final BaseWineRepository baseWineRepository;
    private final CombinationRepository combinationRepository;

    public DataSeeder(SeedProperties seedProperties,
                      RecipeRepository recipeRepository,
                      BaseWineRepository baseWineRepository,
                      CombinationRepository combinationRepository) {
        this.seedProperties = seedProperties;
        this.recipeRepository = recipeRepository;
        this.baseWineRepository = baseWineRepository;
        this.combinationRepository = combinationRepository;
    }

    /**
     * 資料庫已有酒譜就略過，避免重複寫入撞到唯一鍵
     *
     * @param args 啟動參數，未使用
     */
    @Override
    @Transactional
    public void run(String... args) {
        long existing = recipeRepository.count();
        if (existing > 0) {
            log.info("Skip seeding, database already has {} recipes", existing);
            return;
        }

        Map<String, BaseWine> baseWines = new HashMap<>();
        Map<CombinationSeed, Combinations> combinations = new HashMap<>();
        for (RecipeSeed seed : seedProperties.recipes()) {
            Recipe recipe = toRecipe(seed, baseWines);
            if (seed.combination() != null) {
                recipe.setCombination(combinations.computeIfAbsent(seed.combination(), this::saveCombination));
            }
            recipeRepository.save(recipe);
        }

        log.info("Seeded {} recipes, {} base wines, {} combinations from seed-data.yaml",
                seedProperties.recipes().size(), baseWines.size(), combinations.size());
    }

    /**
     * @param seed      種子酒譜
     * @param baseWines 已建立的基酒，依名稱共用同一筆
     * @return 尚未存檔的酒譜 Entity，材料靠 cascade 一起寫入
     */
    private Recipe toRecipe(RecipeSeed seed, Map<String, BaseWine> baseWines) {
        Recipe recipe = new Recipe();
        recipe.setEnTitle(seed.enTitle());
        recipe.setZhTitle(seed.zhTitle());
        recipe.setImage(seed.image());
        recipe.setMethod(seed.method());
        recipe.setBaseWines(seed.baseWines().stream()
                .map(name -> baseWines.computeIfAbsent(name, this::saveBaseWine))
                .collect(Collectors.toCollection(ArrayList::new)));
        recipe.setMaterials(seed.materials().stream()
                .map(m -> {
                    Material material = new Material();
                    material.setMaterialName(m.name());
                    material.setMaterialQuantity(m.quantity());
                    material.setRecipe(recipe);
                    return material;
                })
                .collect(Collectors.toCollection(ArrayList::new)));
        return recipe;
    }

    /**
     * @param name 基酒名稱
     * @return 已存檔的基酒
     */
    private BaseWine saveBaseWine(String name) {
        BaseWine baseWine = new BaseWine();
        baseWine.setName(name);
        return baseWineRepository.save(baseWine);
    }

    /**
     * @param seed 四維度組合
     * @return 已存檔的組合
     */
    private Combinations saveCombination(CombinationSeed seed) {
        Combinations combination = new Combinations();
        combination.setMood(seed.mood());
        combination.setTaste(seed.taste());
        combination.setTone(seed.tone());
        combination.setDrunk(seed.drunk());
        return combinationRepository.save(combination);
    }
}
