package com.ch.cs_collectiontool.bean;

import java.util.List;

public class Region implements Cloneable {
    private String regionName;
    private List<Street> streets;

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public List<Street> getStreets() {
        return streets;
    }

    public void setStreets(List<Street> streets) {
        this.streets = streets;
    }

    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }
}
