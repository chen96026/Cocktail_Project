package com.example.cocktail.Service;

import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.DTO.CocktailBasicDTO;
import com.example.cocktail.DTO.CocktailSelectorDTO;
import com.example.cocktail.Repository.CocktailSelectorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CocktailSelectorService {

    private final CocktailSelectorRepository cocktailSelectorRepository;

    public CocktailSelectorService(CocktailSelectorRepository cocktailSelectorRepository) {
        this.cocktailSelectorRepository = cocktailSelectorRepository;
    }

    /**
     * @param selector 篩選器的四個條件
     * @return 符合組合的調酒清單
     */
    public List<CocktailBasicDTO> findRecipesByCombination(CocktailSelectorDTO selector) {
        return cocktailSelectorRepository.findRecipesByCombination(
                selector.mood(), selector.taste(), selector.tone(), selector.drunk());
    }

    /**
     * @param recipeId 酒譜 ID
     * @return 該調酒的詳細資料（材料另外查詢後補上）
     */
    public CockTailDetailDTO getCocktailDetail(Integer recipeId) {
        CockTailDetailDTO detail = cocktailSelectorRepository.findDetailByRecipeId(recipeId);
        return detail.withMaterials(cocktailSelectorRepository.findMaterialsByRecipeId(recipeId));
    }
}
