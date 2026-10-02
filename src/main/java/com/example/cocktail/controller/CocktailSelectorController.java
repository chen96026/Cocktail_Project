package com.example.cocktail.controller;

import com.example.cocktail.dto.CocktailDetailDTO;
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

    /**
     * 四個條件沿用改版前的行為：任一沒帶就不會有組合符合，回空陣列
     *
     * @param mood  心情
     * @param taste 口味
     * @param tone  冷暖
     * @param drunk 醉度
     * @return 符合四維度組合的調酒
     */
    @GetMapping("/selector")
    @Operation(summary = "篩選器符合組合之調酒")
    public ResponseEntity<List<CocktailBasicDTO>> getCocktailSelector(
            @RequestParam(value = "mood", required = false) String mood,
            @RequestParam(value = "taste", required = false) String taste,
            @RequestParam(value = "tone", required = false) String tone,
            @RequestParam(value = "drunk", required = false) String drunk) {
        CocktailSelectorDTO selector = new CocktailSelectorDTO(mood, taste, tone, drunk);
        return ResponseEntity.ok(cocktailSelectorService.findRecipesByCombination(selector));
    }

    @GetMapping("/recipes/{recipeId}/detail")
    @Operation(summary = "篩選器符合調酒之資訊（含組合與材料，沒有分配組合時回 404）")
    public ResponseEntity<CocktailDetailDTO> getCocktailDetail(@PathVariable("recipeId") Integer recipeId) {
        return ResponseEntity.ok(cocktailSelectorService.getCocktailDetail(recipeId));
    }
}
