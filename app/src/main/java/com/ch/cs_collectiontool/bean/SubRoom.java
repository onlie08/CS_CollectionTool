package com.ch.cs_collectiontool.bean;

import java.io.Serializable;
import java.util.PropertyResourceBundle;

public class SubRoom implements Serializable {
    private String roomNo;
    private String ownerName;
//    private boolean enableDelete = false;

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

//    public boolean isEnableDelete() {
//        return enableDelete;
//    }
//
//    public void setEnableDelete(boolean enableDelete) {
//        this.enableDelete = enableDelete;
//    }
}
