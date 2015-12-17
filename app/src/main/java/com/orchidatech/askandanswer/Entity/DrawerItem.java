package com.orchidatech.askandanswer.Entity;

/**
 * Created by Bahaa on 8/11/2015.
 */
public class DrawerItem {
    String title;
    int image;
    int image_on;
    boolean isSelected;

    public DrawerItem(String title, int image, int image_on, boolean isSelected) {
        this.title = title;
        this.image = image;
        this.image_on = image_on;
        this.isSelected = isSelected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getImage_on() {
        return image_on;
    }

    public void setImage_on(int image_on) {
        this.image_on = image_on;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
