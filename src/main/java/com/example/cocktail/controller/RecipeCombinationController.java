package com.example.cocktail.controller;

import com.example.cocktail.dto.AssignmentRequest;
import com.example.cocktail.dto.CocktailDetailDTO;
import com.example.cocktail.service.RecipeCombinationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lastwine/recipe-combinations")
@Tag(name = "Admin", description = "API")
public class RecipeCombinationController {
    private final RecipeCombinationService recipeCombinationService;

    public RecipeCombinationController(RecipeCombinationService recipeCombinationService) {
        this.recipeCombinationService = recipeCombinationService;
    }

    // 查詢所有調酒與組合
    @GetMapping
    @Operation(summary = "後台查詢所有調酒與組合")
    public ResponseEntity<List<CocktailDetailDTO>> getAllRecipeCombinations() {
        List<CocktailDetailDTO> combinations = recipeCombinationService.getAllRecipeCombinations();
        return ResponseEntity.ok(combinations);
    }

    /**
     * 只改有帶到的酒譜，沒帶到的維持原本的組合，屬於部分更新所以用 PATCH
     *
     * @param assignments 要分配的酒譜與組合
     * @return 成功訊息
     */
    @PatchMapping
    @Operation(summary = "後台整批分配調酒與組合（只更新有帶到的酒譜）")
    public ResponseEntity<Map<String, String>> assignAllCombinations(@RequestBody List<AssignmentRequest> assignments) {
        recipeCombinationService.assignCombinations(assignments);
        return ResponseEntity.ok(Map.of("message", "所有組合分配成功"));
    }
}
