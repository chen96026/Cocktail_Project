package com.example.cocktail.DTO;

public class CocktailBasicDTO {
    private Integer recipeId;
    private String zhTitle;
    private String enTitle;
    private String image;

    public CocktailBasicDTO(Integer recipeId, String zhTitle, String enTitle, String image) {
        this.recipeId = recipeId;
        this.zhTitle = zhTitle;
        this.enTitle = enTitle;
        this.image = image;
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
}
