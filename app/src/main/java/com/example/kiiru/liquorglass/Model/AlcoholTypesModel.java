package com.example.kiiru.liquorglass.Model;

/**
 * Created by Kiiru on 10/3/2017.
 */

public class AlcoholTypesModel {
    private String Name;
    private String Image;

    public AlcoholTypesModel() {
    }

    public AlcoholTypesModel(String name, String image) {
        Name = name;
        Image = image;
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
}
