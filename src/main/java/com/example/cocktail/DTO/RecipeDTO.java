package com.example.cocktail.DTO;

import com.example.cocktail.Model.BaseWine;
import com.example.cocktail.Model.Recipe;
import com.example.cocktail.Model.Material;

import java.util.List;
import java.util.stream.Collectors;

public class RecipeDTO {
    private Integer recipe_id;
    private String en_title;
    private String zh_title;
    private String method;
    private String image;
    private List<String> base_wines;
    private List<MaterialDTO> materials;

    public RecipeDTO(Recipe recipe) {
        this.recipe_id = recipe.getRecipe_id();
        this.en_title = recipe.getEn_title();
        this.zh_title = recipe.getZh_title();
        this.method = recipe.getMethod();
        this.image = recipe.getImage();
        this.base_wines = recipe.getBaseWines()
                .stream()
                .map(BaseWine::getName)
                .collect(Collectors.toList());
        // 提取材料名稱和數量，轉換成 MaterialDTO
        this.materials = recipe.getMaterials()
                .stream()
                .map(material -> new MaterialDTO(material.getMaterial_name(), material.getMaterial_quantity()))
                .collect(Collectors.toList());
    }

    public Integer getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(Integer recipe_id) {
        this.recipe_id = recipe_id;
    }

    public String getEn_title() {
        return en_title;
    }

    public void setEn_title(String en_title) {
        this.en_title = en_title;
    }

    public String getZh_title() {
        return zh_title;
    }

    public void setZh_title(String zh_title) {
        this.zh_title = zh_title;
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

    public List<String> getBase_wines() {
        return base_wines;
    }

    public void setBase_wines(List<String> base_wines) {
        this.base_wines = base_wines;
    }

    public List<MaterialDTO> getMaterials() {
        return materials;
    }

    public void setMaterials(List<MaterialDTO> materials) {
        this.materials = materials;
    }
}
