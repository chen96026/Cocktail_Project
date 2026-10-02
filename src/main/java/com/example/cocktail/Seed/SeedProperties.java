package com.example.cocktail.Seed;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 對應 seed-data.yaml 的 cocktail.seed 區塊，由 DataSeeder 載入資料庫
 *
 * @author Harry
 * @since 2026-10-02
 * 異動歷史：2026-10-02 Harry 新建
 *
 * @param recipes 種子酒譜清單
 */
@ConfigurationProperties(prefix = "cocktail.seed")
public record SeedProperties(List<RecipeSeed> recipes) {

    public SeedProperties {
        recipes = recipes == null ? List.of() : recipes;
    }

    /**
     * @param enTitle     英文名稱
     * @param zhTitle     中文名稱
     * @param image       圖片 URL
     * @param method      介紹與作法
     * @param baseWines   基酒名稱清單
     * @param materials   材料清單
     * @param combination 四維度組合，沒有分配時為 null
     */
    public record RecipeSeed(String enTitle, String zhTitle, String image, String method,
                             List<String> baseWines, List<MaterialSeed> materials,
                             CombinationSeed combination) {

        public RecipeSeed {
            baseWines = baseWines == null ? List.of() : baseWines;
            materials = materials == null ? List.of() : materials;
        }
    }

    /**
     * @param name     材料名稱
     * @param quantity 份量
     */
    public record MaterialSeed(String name, String quantity) {
    }

    /**
     * 同一組四維度在 record 的 equals 下視為相同，DataSeeder 用它去重
     *
     * @param mood  心情
     * @param taste 口味
     * @param tone  冷暖
     * @param drunk 醉度
     */
    public record CombinationSeed(String mood, String taste, String tone, String drunk) {
    }
}
