package com.example.cocktail.repository;

import com.example.cocktail.model.BaseWine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BaseWineRepository extends JpaRepository<BaseWine, Integer> {
    public Optional<BaseWine> findByName(String name);
}