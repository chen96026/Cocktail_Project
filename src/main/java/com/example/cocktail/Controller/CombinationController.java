package com.example.cocktail.Controller;

import com.example.cocktail.Model.Combinations;
import com.example.cocktail.Repository.CombinationRepository;
import com.example.cocktail.Service.CombinationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/lastwine")
@Tag(name = "Admin", description = "API")
public class CombinationController {

    @Autowired
    private CombinationService combinationService;

    @Autowired
    private CombinationRepository combinationRepository;

    @PostMapping("/addFourCombination")
    @Operation(summary = "後台加入組合至調酒")
    public ResponseEntity<String> addCombination(@RequestBody Combinations combination) {
        try {
            combinationService.addCombination(combination);
            return ResponseEntity.ok("組合新增成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("新增組合失敗: " + e.getMessage());
        }
    }

    @GetMapping("/getAllCombinations")
    @Operation(summary = "後台取得組合")
    public ResponseEntity<List<Combinations>> getAllCombinations() {
        List<Combinations> combinations = combinationRepository.findAll();
        return ResponseEntity.ok(combinations);
    }
}
