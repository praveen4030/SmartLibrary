package com.example.smartlibrary.Model;

public class Item {

    String name,image,color,text,url;

    public Item(){}

    public Item(String name, String image, String url) {
        this.name = name;
        this.image = image;
        this.url = url;
    }

    public Item(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
