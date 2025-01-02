package com.example.cocktail.Service;

import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.DTO.CocktailBasicDTO;
import com.example.cocktail.DTO.CocktailSelectorDTO;
import com.example.cocktail.DTO.MaterialDTO;
import com.example.cocktail.Repository.CocktailSelectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CocktailSelectorServiceImpl implements CocktailSelectorService{

    @Autowired
    private CocktailSelectorRepository cocktailselectorRepository;

    @Override
    public List<CocktailBasicDTO> findRecipesByCombination(CocktailSelectorDTO CocktailSelectorDTO) {
        return cocktailselectorRepository.findRecipesByCombination(
                CocktailSelectorDTO.getMood(),
                CocktailSelectorDTO.getTaste(),
                CocktailSelectorDTO.getTone(),
                CocktailSelectorDTO.getDrunk()
        );
    }

    @Override
    public CockTailDetailDTO getCocktailDetail(Integer recipeId) {
        CockTailDetailDTO detail = cocktailselectorRepository.findDetailByRecipeId(recipeId);
        List<MaterialDTO> materials = cocktailselectorRepository.findMaterialsByRecipeId(recipeId);
        detail.setMaterials(materials);
        return detail;
    }
}
