package com.example.cocktail.DTO;

import com.example.cocktail.Model.Combinations;

/**
 * 後台組合列表的回傳資料，欄位名沿用既有 API 契約
 *
 * @author Harry
 * @since 2026-10-02
 * 異動歷史：2026-10-02 Harry 新建
 *
 * @param combinationId 組合 ID
 * @param mood          心情
 * @param taste         口味
 * @param tone          冷暖
 * @param drunk         醉度
 */
public record CombinationDTO(Integer combinationId, String mood, String taste, String tone, String drunk) {

    /**
     * @param combination 來源 Entity
     * @return 對應的 DTO
     */
    public static CombinationDTO from(Combinations combination) {
        return new CombinationDTO(
                combination.getCombinationId(),
                combination.getMood(),
                combination.getTaste(),
                combination.getTone(),
                combination.getDrunk()
        );
    }
}
