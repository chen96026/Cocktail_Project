package com.example.cocktail.controller;

import com.example.cocktail.dto.CombinationDTO;
import com.example.cocktail.dto.CombinationRequest;
import com.example.cocktail.service.CombinationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lastwine")
@Tag(name = "Admin", description = "API")
public class CombinationController {

    private final CombinationService combinationService;

    public CombinationController(CombinationService combinationService) {
        this.combinationService = combinationService;
    }

    @PostMapping("/addFourCombination")
    @Operation(summary = "後台加入組合至調酒")
    public ResponseEntity<Map<String, String>> addCombination(@RequestBody CombinationRequest request) {
        combinationService.addCombination(request);
        return ResponseEntity.ok(Map.of("message", "組合新增成功"));
    }

    @GetMapping("/getAllCombinations")
    @Operation(summary = "後台取得組合")
    public ResponseEntity<List<CombinationDTO>> getAllCombinations() {
        List<CombinationDTO> combinations = combinationService.getAllCombinations();
        return ResponseEntity.ok(combinations);
    }
}
