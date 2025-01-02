package com.example.cocktail.Repository;

import com.example.cocktail.Model.Material;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material,Integer> {
    public List<Material> findByRecipe_recipeId(Integer recipeId);

}
