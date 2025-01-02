package com.example.cocktail.Controller;

import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.Model.CombinationOption;
import com.example.cocktail.Service.CombinationOptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lastwine")
@Tag(name = "Admin", description = "API")
public class CombinationOptionController {
    @Autowired
    private CombinationOptionService combinationOptionService;

    // 查詢所有調酒與組合
    @GetMapping("/allCombinations")
    @Operation(summary = "後台查詢所有調酒與組合")
    public ResponseEntity<List<CockTailDetailDTO>> getAllRecipeCombinations() {
        List<CockTailDetailDTO> combinations = combinationOptionService.getAllRecipeCombinations();
        return ResponseEntity.ok(combinations);
    }

    // 分配組合給調酒
    @PostMapping("/assignCombinations")
    @Operation(summary = "後台分配調酒與組合")
    public ResponseEntity<Map<String, String>> assignAllCombinations(@RequestBody List<CombinationOption> combinationOptions) {
        for (CombinationOption combinationOption : combinationOptions) {
            // 刪除舊的組合
            combinationOptionService.removeCombination(combinationOption.getFkRecipeId().getRecipe_id());
            // 新增新的組合
            combinationOptionService.assignCombination(combinationOption);
        }
        Map<String, String> response = new HashMap<>();
        response.put("message", "所有組合分配成功");
        return ResponseEntity.ok(response);
    }
}
