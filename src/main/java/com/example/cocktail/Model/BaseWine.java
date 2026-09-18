package com.example.cocktail.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "basewine")
public class BaseWine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// 可以自動遞增
    private Integer baseWineId;

    @Column(unique = true)
    private String name;

    public Integer getBaseWineId() {
        return baseWineId;
    }

    public void setBaseWineId(Integer baseWineId) {
        this.baseWineId = baseWineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
