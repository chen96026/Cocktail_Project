package com.example.cocktail.controller;

import com.example.cocktail.dto.CockTailDetailDTO;
import com.example.cocktail.dto.CocktailBasicDTO;
import com.example.cocktail.dto.CocktailSelectorDTO;
import com.example.cocktail.service.CocktailSelectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lastwine")
@Tag(name = "CocktailSelector", description = "API")
public class CocktailSelectorController {

    private final CocktailSelectorService cocktailSelectorService;

    public CocktailSelectorController(CocktailSelectorService cocktailSelectorService) {
        this.cocktailSelectorService = cocktailSelectorService;
    }

    @PostMapping("/getCocktailSelector")
    @Operation(summary = "篩選器符合組合之調酒")
    public ResponseEntity<List<CocktailBasicDTO>> getCocktailSelector(@RequestBody CocktailSelectorDTO selector) {
        return ResponseEntity.ok(cocktailSelectorService.findRecipesByCombination(selector));
    }

    @GetMapping("/getCocktailDetail/{recipeId}")
    @Operation(summary = "篩選器符合調酒之資訊")
    public ResponseEntity<CockTailDetailDTO> getCocktailDetail(@PathVariable Integer recipeId) {
        return ResponseEntity.ok(cocktailSelectorService.getCocktailDetail(recipeId));
    }
}
