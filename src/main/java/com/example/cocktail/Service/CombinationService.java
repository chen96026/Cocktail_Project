package com.example.cocktail.Service;

import com.example.cocktail.Exception.BusinessException;
import com.example.cocktail.Model.Combinations;
import com.example.cocktail.Repository.CombinationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CombinationService {

    private final CombinationRepository combinationRepository;

    public CombinationService(CombinationRepository combinationRepository) {
        this.combinationRepository = combinationRepository;
    }

    /**
     * @param combination 要新增的四維度組合
     */
    public void addCombination(Combinations combination) {
        // 檢查組合是否存在
        boolean exists = combinationRepository.existsByMoodAndTasteAndToneAndDrunk(
                combination.getMood(), combination.getTaste(), combination.getTone(), combination.getDrunk());
        if (exists) {
            throw new BusinessException("組合已存在，無法重複新增");
        }
        combinationRepository.save(combination);
    }

    /**
     * @return 所有已建立的四維度組合
     */
    public List<Combinations> getAllCombinations() {
        return combinationRepository.findAll();
    }
}
