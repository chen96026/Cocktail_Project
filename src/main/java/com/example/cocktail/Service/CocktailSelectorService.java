package com.example.cocktail.Service;

import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.DTO.CocktailBasicDTO;
import com.example.cocktail.DTO.CocktailSelectorDTO;
import com.example.cocktail.Exception.NotFoundException;
import com.example.cocktail.Repository.MaterialRepository;
import com.example.cocktail.Repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CocktailSelectorService {

    private final RecipeRepository recipeRepository;
    private final MaterialRepository materialRepository;

    public CocktailSelectorService(RecipeRepository recipeRepository, MaterialRepository materialRepository) {
        this.recipeRepository = recipeRepository;
        this.materialRepository = materialRepository;
    }

    /**
     * @param selector 篩選器的四個條件
     * @return 符合組合的調酒清單
     */
    public List<CocktailBasicDTO> findRecipesByCombination(CocktailSelectorDTO selector) {
        return recipeRepository.findRecipesByCombination(
                selector.mood(), selector.taste(), selector.tone(), selector.drunk());
    }

    /**
     * @param recipeId 酒譜 ID
     * @return 該調酒的詳細資料（材料另外查詢後補上）
     */
    public CockTailDetailDTO getCocktailDetail(Integer recipeId) {
        CockTailDetailDTO detail = recipeRepository.findDetailByRecipeId(recipeId);
        if (detail == null) {
            throw new NotFoundException("找不到該調酒的組合資料，ID: " + recipeId);
        }
        return detail.withMaterials(materialRepository.findMaterialsByRecipeId(recipeId));
    }
}
