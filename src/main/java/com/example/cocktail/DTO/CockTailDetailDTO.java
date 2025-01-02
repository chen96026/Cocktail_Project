package com.example.cocktail.DTO;

import java.util.List;

public class CockTailDetailDTO {
    private Integer recipeId;
    private String image;
    private String enTitle;
    private String zhTitle;
    private List<MaterialDTO> materials;
    private String method;
    private Integer combinationId;
    private String mood;
    private String taste;
    private String tone;
    private String drunk;

    public CockTailDetailDTO(Integer recipeId, String image, String enTitle, String zhTitle, String method, Integer combinationId, String mood, String taste, String tone, String drunk) {
        this.recipeId = recipeId;
        this.image = image;
        this.enTitle = enTitle;
        this.zhTitle = zhTitle;
        this.method = method;
        this.combinationId = combinationId;
        this.mood = mood;
        this.taste = taste;
        this.tone = tone;
        this.drunk = drunk;
    }

    public Integer getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public Integer getCombinationId() {
        return combinationId;
    }

    public void setCombinationId(Integer combinationId) {
        this.combinationId = combinationId;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getTaste() {
        return taste;
    }

    public void setTaste(String taste) {
        this.taste = taste;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public String getDrunk() {
        return drunk;
    }

    public void setDrunk(String drunk) {
        this.drunk = drunk;
    }
}
