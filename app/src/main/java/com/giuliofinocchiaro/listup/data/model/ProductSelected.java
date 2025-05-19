package com.giuliofinocchiaro.listup.data.model;

public class ProductSelected extends Product{
    private int id_selected;
    private ListShop list;
    private User user;
    private String option;
    private String description;
    private double quantity;
    private boolean is_urgent;
    private boolean is_on_offer;
    private boolean is_worth_it;
    private boolean is_checked;

    public ProductSelected(Product product, int id, ListShop list, User user, String option, double quantity, String description, boolean is_urgent, boolean is_worth_it, boolean is_on_offer, boolean is_checked) {
        super(product);
        this.id_selected = id;
        this.list = list;
        this.user = user;
        this.option = option;
        this.quantity = quantity;
        this.description = description;
        this.is_urgent = is_urgent;
        this.is_worth_it = is_worth_it;
        this.is_on_offer = is_on_offer;
        this.is_checked = is_checked;
    }

    public int getId_selected() {
        return id_selected;
    }

    public ListShop getList() {
        return list;
    }

    public User getUser() {
        return user;
    }

    public String getDescription() {
        return description;
    }

    public String getOption() {
        return option;
    }

    public double getQuantity() {
        return quantity;
    }

    public boolean isIs_urgent() {
        return is_urgent;
    }

    public boolean isIs_on_offer() {
        return is_on_offer;
    }

    public boolean isIs_worth_it() {
        return is_worth_it;
    }

    public boolean isIs_checked() {
        return is_checked;
    }
}
