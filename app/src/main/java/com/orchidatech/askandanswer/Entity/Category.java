package com.orchidatech.askandanswer.Entity;

/**
 * Created by Bahaa on 6/11/2015.
 */
public class Category {
    String name;
    boolean checked;

    public Category(String name, boolean checked) {
        this.name = name;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
