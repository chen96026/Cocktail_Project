package com.example.cocktail.controller;

import com.example.cocktail.dto.CombinationDTO;
import com.example.cocktail.dto.CombinationRequest;
import com.example.cocktail.service.CombinationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lastwine/combinations")
@Tag(name = "Admin", description = "API")
public class CombinationController {

    private final CombinationService combinationService;

    public CombinationController(CombinationService combinationService) {
        this.combinationService = combinationService;
    }

    /**
     * 沒有單筆查詢的端點可以指，所以不帶 Location
     *
     * @param request 要新增的四維度組合
     * @return 201 與成功訊息
     */
    @PostMapping
    @Operation(summary = "後台新增四維度組合")
    @ApiResponse(responseCode = "201", description = "新增成功")
    public ResponseEntity<Map<String, String>> addCombination(@RequestBody CombinationRequest request) {
        combinationService.addCombination(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "組合新增成功"));
    }

    @GetMapping
    @Operation(summary = "後台取得組合")
    public ResponseEntity<List<CombinationDTO>> getAllCombinations() {
        List<CombinationDTO> combinations = combinationService.getAllCombinations();
        return ResponseEntity.ok(combinations);
    }
}
