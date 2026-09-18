package com.example.cocktail.Controller;

import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.Model.CombinationOption;
import com.example.cocktail.Service.CombinationOptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lastwine")
@Tag(name = "Admin", description = "API")
public class CombinationOptionController {
    private final CombinationOptionService combinationOptionService;

    public CombinationOptionController(CombinationOptionService combinationOptionService) {
        this.combinationOptionService = combinationOptionService;
    }

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
        combinationOptionService.assignCombinations(combinationOptions);
        return ResponseEntity.ok(Map.of("message", "所有組合分配成功"));
    }
}
