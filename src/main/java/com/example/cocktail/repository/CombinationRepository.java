package com.example.cocktail.repository;

import com.example.cocktail.model.Combinations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CombinationRepository extends JpaRepository<Combinations, Integer> {
    // 後台新增組合前查詢是否有存在的
    public boolean existsByMoodAndTasteAndToneAndDrunk(String mood, String taste, String tone, String drunk);


}
