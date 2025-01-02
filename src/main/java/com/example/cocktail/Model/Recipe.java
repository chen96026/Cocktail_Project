package com.example.cocktail.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @ManyToOne(fetch = FetchType.LAZY) // 多篇酒譜對一個會員
    @JoinColumn(name = "member_id") // 外鍵關聯到Member
    @JsonIgnore // 防止傳遞所有會員資訊
    private Member member;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "basewinelist", // 關聯表名稱
            joinColumns = @JoinColumn(name = "recipe_id"),// 連接到Recipe的外鍵，當前實體的，主控方
            inverseJoinColumns = @JoinColumn(name = "base_wine_id")// 連接到BaseWine的外鍵，被控方
    )
    private List<BaseWine> baseWines = new ArrayList<>();

    public Integer getRecipe_id() {
        return recipeId;
    }

    public void setRecipe_id(Integer recipe_id) {
        this.recipeId = recipe_id;
    }

    public String getEn_title() {
        return enTitle;
    }

    public void setEn_title(String en_title) {
        this.enTitle = en_title;
    }

    public String getZh_title() {
        return zhTitle;
    }

    public void setZh_title(String zh_title) {
        this.zhTitle = zh_title;
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

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
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