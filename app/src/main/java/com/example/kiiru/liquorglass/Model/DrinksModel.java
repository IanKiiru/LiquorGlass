package com.example.kiiru.liquorglass.Model;

/**
 * Created by Kiiru on 10/4/2017.
 */

public class DrinksModel {

    private String Name, Image, Price, TypeId;

    public DrinksModel() {
    }

    public DrinksModel(String name, String image, String price, String typeId) {
        Name = name;
        Image = image;
        Price = price;
        TypeId = typeId;
    }

    public String getTypeId() {
        return TypeId;
    }

    public void setTypeId(String typeId) {
        TypeId = typeId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }


}
