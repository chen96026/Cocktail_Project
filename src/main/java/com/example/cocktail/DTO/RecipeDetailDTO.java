package com.example.cocktail.DTO;

import java.util.List;

public class RecipeDetailDTO {
    private Integer recipeId;
    private String zhTitle;
    private String enTitle;
    private String image;
    private List<MaterialDTO> materials;
    private String method;

    public RecipeDetailDTO(Integer recipeId, String zhTitle, String enTitle, String image, List<MaterialDTO> materials, String method) {
        this.recipeId = recipeId;
        this.zhTitle = zhTitle;
        this.enTitle = enTitle;
        this.image = image;
        this.materials = materials;
        this.method = method;
    }

    public Integer getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }

    public String getZhTitle() {
        return zhTitle;
    }

    public void setZhTitle(String zhTitle) {
        this.zhTitle = zhTitle;
    }

    public String getEnTitle() {
        return enTitle;
    }

    public void setEnTitle(String enTitle) {
        this.enTitle = enTitle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<MaterialDTO> getMaterials() {
        return materials;
    }

    public void setMaterials(List<MaterialDTO> materials) {
        this.materials = materials;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
