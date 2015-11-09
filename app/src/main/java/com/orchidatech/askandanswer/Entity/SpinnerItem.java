package com.orchidatech.askandanswer.Entity;

/**
 * Created by Bahaa on 8/11/2015.
 */
public class SpinnerItem {
    String name;
    boolean isHint;

    public SpinnerItem(String name, boolean isHint) {
        this.name = name;
        this.isHint = isHint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHint() {
        return isHint;
    }

    public void setIsHint(boolean isHint) {
        this.isHint = isHint;
    }
}
