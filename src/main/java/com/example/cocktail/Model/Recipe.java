package com.example.cocktail.Model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipe")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Integer recipeId;

    @Column(unique = true)
    private String enTitle;

    @Column(unique = true)
    private String zhTitle;

    @Column(columnDefinition = "TEXT")
    private String method;

    @Column
    private String image;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Material> materials = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "basewinelist", // 關聯表名稱
            joinColumns = @JoinColumn(name = "recipe_id"),// 連接到Recipe的外鍵，當前實體的，主控方
            inverseJoinColumns = @JoinColumn(name = "baseWineId")// 連接到BaseWine的外鍵，被控方
    )
    private List<BaseWine> baseWines = new ArrayList<>();

    public Integer getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }

    public String getEnTitle() {
        return enTitle;
    }

    public void setEnTitle(String enTitle) {
        this.enTitle = enTitle;
    }

    public String getZhTitle() {
        return zhTitle;
    }

    public void setZhTitle(String zhTitle) {
        this.zhTitle = zhTitle;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<BaseWine> getBaseWines() {
        return baseWines;
    }

    public void setBaseWines(List<BaseWine> baseWines) {
        this.baseWines = baseWines;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

}