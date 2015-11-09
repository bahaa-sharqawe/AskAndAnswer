package com.orchidatech.askandanswer.Entity;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class Post {
    long id;
    long date;
    String category;
    float rate;
    String desc;
    int comments;
    int likes;
    int unlikes;
    Person owner;

    public Post(long id, long date, String category, float rate, String desc, int comments, int likes, int unlikes, Person owner) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.rate = rate;
        this.desc = desc;
        this.comments = comments;
        this.likes = likes;
        this.unlikes = unlikes;
        this.owner = owner;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getUnlikes() {
        return unlikes;
    }

    public void setUnlikes(int unlikes) {
        this.unlikes = unlikes;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }
}
