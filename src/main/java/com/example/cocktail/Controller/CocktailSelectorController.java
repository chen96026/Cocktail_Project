package com.example.cocktail.Controller;

import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.DTO.CocktailBasicDTO;
import com.example.cocktail.DTO.CocktailSelectorDTO;
import com.example.cocktail.Repository.CombinationRepository;
import com.example.cocktail.Service.CocktailSelectorService;
import com.example.cocktail.Service.CombinationService;
import com.example.cocktail.Service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/lastwine")
@Tag(name = "CocktailSelector", description = "API")
public class CocktailSelectorController {

    @Autowired
    private CocktailSelectorService cocktailSelectorService;
    @Autowired
    private RecipeService recipeService;

    @PostMapping("/getCocktailSelector")
    @Operation(summary = "篩選器符合組合之調酒")
    public ResponseEntity<List<CocktailBasicDTO>> getCocktailSelector(@RequestBody CocktailSelectorDTO selector) {
        try {
            List<CocktailBasicDTO> recipes = cocktailSelectorService.findRecipesByCombination(selector);
            return ResponseEntity.ok(recipes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getCocktailDetail/{recipeId}")
    @Operation(summary = "篩選器符合調酒之資訊")
    public ResponseEntity<CockTailDetailDTO> getCocktailDetail(@PathVariable Integer recipeId) {
        try {
            CockTailDetailDTO detail = cocktailSelectorService.getCocktailDetail(recipeId);
            return ResponseEntity.ok(detail);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
