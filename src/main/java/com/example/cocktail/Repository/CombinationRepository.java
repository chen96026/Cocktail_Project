package com.example.cocktail.Repository;

import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.DTO.CocktailBasicDTO;
import com.example.cocktail.Model.Combinations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CombinationRepository extends JpaRepository<Combinations, Integer> {
    // 後台新增組合前查詢是否有存在的
    public boolean existsByMoodAndTasteAndToneAndDrunk(String mood, String taste, String tone, String drunk);


}
