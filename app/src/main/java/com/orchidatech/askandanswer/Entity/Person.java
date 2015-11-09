package com.orchidatech.askandanswer.Entity;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class Person {
    long id;
    String name;
    String photo;
    String email;
    double rate;
    int ask;
    int answer;

    public Person(long id, String name, String photo, String email, double rate, int ask, int answer) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.email = email;
        this.rate = rate;
        this.ask = ask;
        this.answer = answer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getAsk() {
        return ask;
    }

    public void setAsk(int ask) {
        this.ask = ask;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }
}
