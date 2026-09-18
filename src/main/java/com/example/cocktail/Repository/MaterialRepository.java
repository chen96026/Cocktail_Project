package com.example.cocktail.Repository;

import com.example.cocktail.Model.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material,Integer> {
}
