package com.example.cocktail.Repository;

import com.example.cocktail.DTO.CockTailDetailDTO;
import com.example.cocktail.DTO.CocktailBasicDTO;
import com.example.cocktail.DTO.MaterialDTO;
import com.example.cocktail.Model.Combinations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CocktailSelectorRepository extends JpaRepository<Combinations, Integer> {
    // 調酒篩選器
    @Query("SELECT new com.example.cocktail.DTO.CocktailBasicDTO(r.recipeId, r.zhTitle, r.enTitle, r.image) " +
            "FROM Recipe r " +
            "JOIN CombinationOption co ON r.recipeId = co.fkRecipeId.recipeId " +
            "JOIN Combinations c ON co.fkCombinationId.combinationId = c.combinationId " +
            "WHERE c.mood = :mood AND c.taste = :taste AND c.tone = :tone AND c.drunk = :drunk")
    public List<CocktailBasicDTO> findRecipesByCombination(
            @Param("mood") String mood,
            @Param("taste") String taste,
            @Param("tone") String tone,
            @Param("drunk") String drunk
    );

    // 獲取篩選器篩選後的Modal資訊，把材料分開，因為最後是比對Id，不是比對四種篩選，所以可以不用List
    @Query("SELECT new com.example.cocktail.DTO.CockTailDetailDTO(" +
            "r.recipeId, r.image,r.enTitle, r.zhTitle, r.method, " +
            "c.combinationId, c.mood, c.taste, c.tone, c.drunk) " +
            "FROM Recipe r " +
            "JOIN CombinationOption co ON r.recipeId = co.fkRecipeId.recipeId " +
            "JOIN Combinations c ON co.fkCombinationId.combinationId = c.combinationId " +
            "WHERE r.recipeId = :recipeId")
    public CockTailDetailDTO findDetailByRecipeId(@Param("recipeId") Integer recipeId);

    // 查詢材料
    @Query("SELECT new com.example.cocktail.DTO.MaterialDTO(m.material_name, m.material_quantity) " +
            "FROM Material m WHERE m.recipe.recipeId = :recipeId")
    public List<MaterialDTO> findMaterialsByRecipeId(@Param("recipeId") Integer recipeId);
}
