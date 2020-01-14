package com.ch.cs_collectiontool.bean;

public class Street implements Cloneable {
    private String streetName;
    private String villages;

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getVillages() {
        return villages;
    }

    public void setVillages(String villages) {
        this.villages = villages;
    }

    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }
}