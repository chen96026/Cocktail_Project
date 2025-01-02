package com.example.cocktail.DTO;

public class CocktailSelectorDTO {
    private String mood;
    private String taste;
    private String tone;
    private String drunk;

    public CocktailSelectorDTO(String mood, String taste, String tone, String drunk) {
        this.mood = mood;
        this.taste = taste;
        this.tone = tone;
        this.drunk = drunk;
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
