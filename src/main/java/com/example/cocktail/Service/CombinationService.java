package com.example.cocktail.Service;

import com.example.cocktail.DTO.CombinationDTO;
import com.example.cocktail.DTO.CombinationRequest;
import com.example.cocktail.Exception.BusinessException;
import com.example.cocktail.Model.Combinations;
import com.example.cocktail.Repository.CombinationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CombinationService {

    private final CombinationRepository combinationRepository;

    public CombinationService(CombinationRepository combinationRepository) {
        this.combinationRepository = combinationRepository;
    }

    /**
     * @param request 要新增的四維度組合
     */
    @Transactional
    public void addCombination(CombinationRequest request) {
        // 檢查組合是否存在
        boolean exists = combinationRepository.existsByMoodAndTasteAndToneAndDrunk(
                request.mood(), request.taste(), request.tone(), request.drunk());
        if (exists) {
            throw new BusinessException("組合已存在，無法重複新增");
        }
        Combinations combination = new Combinations();
        combination.setMood(request.mood());
        combination.setTaste(request.taste());
        combination.setTone(request.tone());
        combination.setDrunk(request.drunk());
        combinationRepository.save(combination);
    }

    /**
     * @return 所有已建立的四維度組合
     */
    @Transactional(readOnly = true)
    public List<CombinationDTO> getAllCombinations() {
        return combinationRepository.findAll().stream()
                .map(CombinationDTO::from)
                .toList();
    }
}
