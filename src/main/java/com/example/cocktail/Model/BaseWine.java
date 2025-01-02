package com.example.cocktail.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "basewine")
@JsonIgnoreProperties({"recipes"}) // 避免循環引用
public class BaseWine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// 可以自動遞增
    private Integer base_wine_id;

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "baseWines")// 被控方
    @JsonIgnore // 避免遞迴
    private List<Recipe> recipes = new ArrayList<>();

    public Integer getBase_wine_id() {
        return base_wine_id;
    }

    public void setBase_wine_id(Integer base_wine_id) {
        this.base_wine_id = base_wine_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
}
