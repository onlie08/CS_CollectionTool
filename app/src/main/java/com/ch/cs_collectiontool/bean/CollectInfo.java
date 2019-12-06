package com.ch.cs_collectiontool.bean;

import com.google.gson.Gson;

public class CollectInfo {
    private User user;
    private Village village;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Village getVillage() {
        return village;
    }

    public void setVillage(Village village) {
        this.village = village;
    }

    public String toString(){
        return new Gson().toJson(this);
    }
}
