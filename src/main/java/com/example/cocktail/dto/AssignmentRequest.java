package com.example.cocktail.dto;

/**
 * 後台分配組合的請求內容，一筆代表「把這杯調酒分配到這個組合」
 * 欄位可能是 null（前端漏帶或舊格式），由 Service 檢查後回 400
 *
 * @author Harry
 * @since 2026-10-02
 * 異動歷史：2026-10-02 Harry 新建
 *
 * @param recipeId      酒譜 ID
 * @param combinationId 四維度組合 ID
 */
public record AssignmentRequest(Integer recipeId, Integer combinationId) {
}
