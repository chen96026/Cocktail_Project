package com.example.cocktail.DTO;

import java.util.List;

/**
 * 新增／更新酒譜的請求內容（以 multipart 的 recipe part 傳入 JSON，圖片另走 image part）
 */
public record RecipeRequest(String enTitle, String zhTitle, String method,
                            List<String> baseWines, List<MaterialDTO> materials) {
}
