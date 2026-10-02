package com.example.cocktail.controller;

import com.example.cocktail.dto.RecipeDTO;
import com.example.cocktail.dto.RecipeRequest;
import com.example.cocktail.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lastwine/recipes")
@Tag(name = "Recipe", description = "API")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /**
     * @param baseWine 基酒名稱，可逗號分隔或重複帶入，酒譜要全部符合
     * @param keyword  中英文名稱關鍵字
     * @return 符合條件的酒譜，兩個條件都帶時取交集
     */
    @GetMapping
    @Operation(summary = "取得調酒列表，可依基酒與關鍵字篩選")
    public ResponseEntity<List<RecipeDTO>> getRecipes(
            @Parameter(description = "基酒名稱，可逗號分隔或重複帶入；不帶或含 All 表示不篩基酒")
            @RequestParam(value = "baseWine", required = false) List<String> baseWine,
            @Parameter(description = "中英文名稱關鍵字，不分大小寫")
            @RequestParam(value = "keyword", required = false) String keyword) {
        return ResponseEntity.ok(recipeService.findRecipes(baseWine, keyword));
    }

    @GetMapping("/{recipeId}")
    @Operation(summary = "取得單一調酒（編輯用）")
    public ResponseEntity<RecipeDTO> getRecipe(@PathVariable("recipeId") Integer recipeId) {
        return ResponseEntity.ok(recipeService.getRecipe(recipeId));
    }

    /**
     * @param recipe 酒譜內容
     * @param image  酒譜圖片
     * @return 201，Location 指向新酒譜
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "新增調酒")
    @ApiResponse(responseCode = "201", description = "新增成功，Location 指向新酒譜")
    public ResponseEntity<Map<String, String>> addRecipe(
            @RequestPart("recipe") RecipeRequest recipe,
            @RequestPart("image") MultipartFile image) {
        Integer recipeId = recipeService.addRecipe(recipe, image);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{recipeId}")
                .buildAndExpand(recipeId)
                .toUri();
        return ResponseEntity.created(location).body(Map.of("message", "成功添加酒譜！"));
    }

    @PutMapping(value = "/{recipeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "更新調酒內容")
    public ResponseEntity<Map<String, String>> updateRecipe(
            @PathVariable("recipeId") Integer recipeId,
            @RequestPart("recipe") RecipeRequest recipe,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        recipeService.updateRecipe(recipeId, recipe, image);
        return ResponseEntity.ok(Map.of("message", "更新成功"));
    }

    @DeleteMapping("/{recipeId}")
    @Operation(summary = "刪除調酒")
    public ResponseEntity<Map<String, String>> deleteRecipe(@PathVariable("recipeId") Integer recipeId) {
        recipeService.deleteRecipe(recipeId);
        return ResponseEntity.ok(Map.of("message", "成功刪除酒譜"));
    }
}
