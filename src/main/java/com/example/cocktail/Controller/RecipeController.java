package com.example.cocktail.Controller;

import com.example.cocktail.DTO.RecipeDTO;
import com.example.cocktail.Model.Recipe;
import com.example.cocktail.Service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lastwine")
@Tag(name = "Recipe", description = "API")
public class RecipeController {

    @Autowired
    public RecipeService recipeService;

    @PostMapping("/addRecipe")
    @Operation(summary = "新增調酒")
    public ResponseEntity<Map<String, String>> addRecipe(@RequestHeader("Authorization") String authorizationHeader,
                                            @RequestParam("en_title") String enTitle,
                                            @RequestParam("zh_title") String zhTitle,
                                            @RequestParam("base_wine_list") List<String> baseWineList,
                                            @RequestParam("material") List<String> materials,
                                            @RequestParam("method") String method,
                                            @RequestParam("image") MultipartFile image) {
        try {
            String token = authorizationHeader.substring(7);
            recipeService.addRecipe(enTitle, zhTitle, baseWineList, materials, method, image, token);

            return ResponseEntity.ok(Map.of("message", "成功添加酒譜！"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/getAllRecipe")
    @Operation(summary = "取得所有調酒")
    public ResponseEntity<List<RecipeDTO>> getAllRecipe() {
        List<RecipeDTO> recipeDTOs = recipeService.getAllRecipes();
        return ResponseEntity.ok(recipeDTOs);
    }

    @GetMapping("/getRecipesByBaseWine")
    @Operation(summary = "根據基酒回傳調酒")
    public ResponseEntity<List<RecipeDTO>> getRecipesByBaseWine(@RequestParam("baseWine") String baseWine) {
        List<String> baseWineList = Arrays.asList(baseWine.split(","));
        List<RecipeDTO> recipeDTOs = recipeService.getRecipesByBaseWine(baseWineList);
        return ResponseEntity.ok(recipeDTOs);
    }

    @PutMapping("/updateRecipe/{recipe_id}")
    @Operation(summary = "更新調酒內容")
    public ResponseEntity<Map<String, String>> updateRecipe(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer recipe_id,
            @RequestParam("en_title") String enTitle,
            @RequestParam("zh_title") String zhTitle,
            @RequestParam("method") String method,
            @RequestParam(value ="image", required = false) MultipartFile image,
            @RequestParam("base_wine_list") List<String> baseWineList,
            @RequestParam("material") List<String> materials) {
        try {
            String token = authorizationHeader.substring(7);
            recipeService.updateRecipe(recipe_id, enTitle, zhTitle, method, image, baseWineList, materials, token);

            return ResponseEntity.ok(Map.of("message", "更新成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "更新失敗：" + e.getMessage()));
        }
    }

    @DeleteMapping("/deleteRecipe/{recipe_id}")
    @Operation(summary = "刪除調酒")
    public ResponseEntity<String> deleteRecipe(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer recipe_id) {
        try {
            // 驗證授權標頭是否有效
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未提供有效的授權標頭");
            }
            // 提取Token
            String token = authorizationHeader.substring(7);
            recipeService.deleteRecipe(recipe_id, token);
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
        List<Recipe> recipes = recipeService.searchRecipes(keyword);
        return ResponseEntity.ok(recipes);
    }
}
