package com.example.cocktail.DTO;

/**
 * 材料（名稱 + 數量）
 * 同時用於回傳與新增/更新酒譜的請求內容，欄位名沿用既有 API 契約
 */
public record MaterialDTO(String materialName, String materialQuantity) {
}
