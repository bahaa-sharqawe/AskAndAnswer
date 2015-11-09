package com.orchidatech.askandanswer.Entity;

/**
 * Created by Bahaa on 8/11/2015.
 */
public class DrawerItem {
    String title;
    int image;

    public DrawerItem(String title, int image) {
        this.title = title;
        this.image = image;
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
}
