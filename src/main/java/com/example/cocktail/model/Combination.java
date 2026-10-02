package com.example.cocktail.model;

import jakarta.persistence.*;

/**
 * 四維度組合（心情／口味／冷暖／醉度），一組可被多杯調酒共用
 * 類別名用單數，table 名稱維持 combinations，不影響 MySQL 既有資料表
 */
@Entity
@Table(name = "combinations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"mood", "taste", "tone", "drunk"})
})
public class Combination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="combination_id")
    private int combinationId;

    @Column(name="mood")
    private String mood;

    @Column(name="taste")
    private String taste;

    @Column(name="tone")
    private String tone;

    @Column(name="drunk")
    private String drunk;

    public int getCombinationId() {
        return combinationId;
    }

    public void setCombinationId(int combinationId) {
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
