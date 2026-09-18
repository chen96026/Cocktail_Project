package com.example.cocktail.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "basewine")
public class BaseWine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// 可以自動遞增
    private Integer base_wine_id;

    @Column(unique = true)
    private String name;

    public Integer getBase_wine_id() {
        return base_wine_id;
    }

    public void setBase_wine_id(Integer base_wine_id) {
        this.base_wine_id = base_wine_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
