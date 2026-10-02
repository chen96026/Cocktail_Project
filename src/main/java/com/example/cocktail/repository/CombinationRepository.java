package com.example.cocktail.repository;

import com.example.cocktail.model.Combination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CombinationRepository extends JpaRepository<Combination, Integer> {
    // 後台新增組合前查詢是否有存在的
    public boolean existsByMoodAndTasteAndToneAndDrunk(String mood, String taste, String tone, String drunk);


}
