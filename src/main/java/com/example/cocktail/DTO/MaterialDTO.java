package com.example.cocktail.DTO;

public class MaterialDTO {
    private String material_name;
    private String material_quantity;

    public MaterialDTO(String material_name, String material_quantity) {
        this.material_name = material_name;
        this.material_quantity = material_quantity;
    }

    public String getMaterial_name() {
        return material_name;
    }

    public void setMaterial_name(String material_name) {
        this.material_name = material_name;
    }

    public String getMaterial_quantity() {
        return material_quantity;
    }

    public void setMaterial_quantity(String material_quantity) {
        this.material_quantity = material_quantity;
    }
}
