package com.example.cocktail.Repository;

import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.Model.CombinationOption;
import com.example.cocktail.Model.Recipe;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CombinationOptionRepository extends JpaRepository<CombinationOption, Integer> {

    // 查詢所有組合的調酒
    @Query("SELECT new com.example.cocktail.DTO.CockTailDetailDTO(r.recipeId, r.image, r.enTitle, r.zhTitle, r.method, c.combinationId, c.mood, c.taste, c.tone, c.drunk) " +
            "FROM Recipe r " +
            "LEFT JOIN CombinationOption co ON r.recipeId = co.fkRecipeId.recipeId " +
            "LEFT JOIN Combinations c ON co.fkCombinationId.combinationId = c.combinationId")
    public List<CockTailDetailDTO> findRecipeCombinationDetails();

    // 刪除單一組合，目的為改成新的組合前先刪除
    @Modifying
    @Query("DELETE FROM CombinationOption co WHERE co.fkRecipeId = :recipe")
    @Transactional
    public void deleteByFkRecipeId(@Param("recipe") Recipe recipe);

}
