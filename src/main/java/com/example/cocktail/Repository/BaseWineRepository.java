package com.example.cocktail.Repository;

import com.example.cocktail.Model.BaseWine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BaseWineRepository extends JpaRepository<BaseWine, Integer> {
    public Optional<BaseWine> findByName(String name);
}