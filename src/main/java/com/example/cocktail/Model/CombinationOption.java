package com.example.cocktail.Model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name="combination_option")
public class CombinationOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="option_id")
    private int optionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="fk_recipe_id",referencedColumnName = "recipe_id")
    // 刪酒譜時由 DB 連帶刪掉組合關聯，跟 MySQL 既有 FK 的 ON DELETE CASCADE 一致
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Recipe fkRecipeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="fk_combination_id", referencedColumnName = "combination_id")
    private Combinations fkCombinationId;

    public int getOptionId() {
        return optionId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    public Recipe getFkRecipeId() {
        return fkRecipeId;
    }

    public void setFkRecipeId(Recipe fkRecipeId) {
        this.fkRecipeId = fkRecipeId;
    }

    public Combinations getFkCombinationId() {
        return fkCombinationId;
    }

    public void setFkCombinationId(Combinations fkCombinationId) {
        this.fkCombinationId = fkCombinationId;
    }
}
