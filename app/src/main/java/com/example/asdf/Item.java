package com.example.asdf;

import java.util.Objects;

// btw ez egy record-al sokkal szebb lenne, de követelményekben class-al kérik :/
public class Item {
    private final String description;
    private final String id;
    private final String name;
    private final String picture;
    private final String price;

    public Item(String description, String id, String name, String picture, String price) {
        this.description = description;
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.price = price;
    }

    public Item() {
        description = "";
        id = "";
        name = "";
        picture = "";
        price = "";
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }

    public String getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(getId(), item.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
