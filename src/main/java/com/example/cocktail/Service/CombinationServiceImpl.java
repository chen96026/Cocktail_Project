package com.example.cocktail.Service;

import com.example.cocktail.DTO.CocktailBasicDTO;
import com.example.cocktail.DTO.CocktailSelectorDTO;
import com.example.cocktail.Model.Combinations;
import com.example.cocktail.Repository.CombinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CombinationServiceImpl implements CombinationService {

    @Autowired
    private CombinationRepository combinationRepository;

    @Override
    public void addCombination(Combinations combination) {
        // 檢查組合是否存在
        if (isCombinationExists(combination.getMood(), combination.getTaste(), combination.getTone(), combination.getDrunk())) {
            throw new IllegalArgumentException("組合已存在，無法重複新增");
        }
        combinationRepository.save(combination);
    }

    @Override
    public boolean isCombinationExists(String mood, String taste, String tone, String drunk) {
        return combinationRepository.existsByMoodAndTasteAndToneAndDrunk(mood, taste, tone, drunk);
    }
}
