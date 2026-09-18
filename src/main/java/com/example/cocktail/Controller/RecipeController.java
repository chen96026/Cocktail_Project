package com.example.cocktail.Controller;

import com.example.cocktail.DTO.RecipeDTO;
import com.example.cocktail.DTO.RecipeRequest;
import com.example.cocktail.Model.Recipe;
import com.example.cocktail.Service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lastwine")
@Tag(name = "Recipe", description = "API")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping(value = "/addRecipe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "新增調酒")
    public ResponseEntity<Map<String, String>> addRecipe(
            @RequestPart("recipe") RecipeRequest recipe,
            @RequestPart("image") MultipartFile image) {
        try {
            recipeService.addRecipe(recipe, image);
            return ResponseEntity.ok(Map.of("message", "成功添加酒譜！"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/getAllRecipe")
    @Operation(summary = "取得所有調酒")
    public ResponseEntity<List<RecipeDTO>> getAllRecipe() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/getRecipesByBaseWine")
    @Operation(summary = "根據基酒回傳調酒")
    public ResponseEntity<List<RecipeDTO>> getRecipesByBaseWine(@RequestParam("baseWine") String baseWine) {
        List<String> baseWineList = Arrays.asList(baseWine.split(","));
        return ResponseEntity.ok(recipeService.getRecipesByBaseWine(baseWineList));
    }

    @PutMapping(value = "/updateRecipe/{recipe_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "更新調酒內容")
    public ResponseEntity<Map<String, String>> updateRecipe(
            @PathVariable Integer recipe_id,
            @RequestPart("recipe") RecipeRequest recipe,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            recipeService.updateRecipe(recipe_id, recipe, image);
            return ResponseEntity.ok(Map.of("message", "更新成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "更新失敗：" + e.getMessage()));
        }
    }

    @DeleteMapping("/deleteRecipe/{recipe_id}")
    @Operation(summary = "刪除調酒")
    public ResponseEntity<String> deleteRecipe(@PathVariable Integer recipe_id) {
        try {
            recipeService.deleteRecipe(recipe_id);
            return ResponseEntity.ok("成功刪除酒譜");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("刪除失敗：" + e.getMessage());
        }
    }

    @GetMapping("/findRecipeId/{recipe_id}")
    @Operation(summary = "找到該調酒(編輯)")
    public ResponseEntity<Recipe> findRecipe(@PathVariable Integer recipe_id) {
        Recipe recipe = recipeService.getRecipe(recipe_id);
        if (recipe == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(recipe);
    }

    @GetMapping("/search")
    @Operation(summary = "搜尋功能")
    public ResponseEntity<List<Recipe>> searchRecipes(@RequestParam String keyword) {
        return ResponseEntity.ok(recipeService.searchRecipes(keyword));
    }
}
