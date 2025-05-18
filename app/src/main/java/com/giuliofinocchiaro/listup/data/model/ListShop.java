package com.giuliofinocchiaro.listup.data.model;

public class ListShop {
    private int id;
    private User owner;
    private String title;
    private String code;
    private boolean canEdit;

    public ListShop(int id, User owner, String title, String code, boolean canEdit) {
        this.id = id;
        this.owner = owner;
        this.title = title;
        this.code = code;
        this.canEdit = canEdit;
    }

    public int getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
    }

    public String getCode() {
        return code;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    @Override
    public String toString() {
        return "ListShop{" +
                "id=" + id +
                ", owner=" + owner +
                ", title='" + title + '\'' +
                ", code='" + code + '\'' +
                ", canEdit=" + canEdit +
                '}';
    }
}
