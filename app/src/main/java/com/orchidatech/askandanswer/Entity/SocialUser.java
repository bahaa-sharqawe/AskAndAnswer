package com.orchidatech.askandanswer.Entity;

/**
 * Created by tareq on 06/11/2015.
 */
public class SocialUser {

    public String email;
    public String name;
    public String id;

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public int getNetwork() {
        return network;
    }

    public void setNetwork(int network) {
        this.network = network;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int network;
    public String avatarURL;

    public static class NetworkType {
        public static final int GOOGLEPLUS = 1;
        public static final int FACEBOOK = 2;
    }
}