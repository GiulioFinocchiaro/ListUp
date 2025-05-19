package com.giuliofinocchiaro.listup.data.model;

public class Product {
    private int id;
    private String name;
    private Category category;
    private String unit;
    private String icon;
    private boolean is_available;

    public Product(int id, String name, Category category, String unit, boolean is_available, String icon) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.is_available = is_available;
        this.icon = icon;
    }

    public Product(Product product) {
        this.id = product.getId();
        this.icon = product.getIcon();
        this.name = product.getName();
        this.unit = product.getUnit();
        this.category = product.getCategory();
        this.is_available = product.isIs_available();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getUnit() {
        return unit;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isIs_available() {
        return is_available;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", unit='" + unit + '\'' +
                ", icon='" + icon + '\'' +
                ", is_available=" + is_available +
                '}';
    }
}
