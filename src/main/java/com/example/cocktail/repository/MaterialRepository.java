package com.example.cocktail.repository;

import com.example.cocktail.dto.MaterialDTO;
import com.example.cocktail.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material,Integer> {

    // 查詢某支酒譜的材料
    @Query("SELECT new com.example.cocktail.dto.MaterialDTO(m.materialName, m.materialQuantity) " +
            "FROM Material m WHERE m.recipe.recipeId = :recipeId")
    public List<MaterialDTO> findMaterialsByRecipeId(@Param("recipeId") Integer recipeId);
}
