package com.ch.cs_collectiontool.bean;

import com.sfmap.api.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;

public class Room implements Serializable {
    private int roomNo;
    private String ownerName;
    private String remark;
    private int gid;
    private String belongGroup;
    private boolean reserve;
    private List<SubRoom> subRooms;

    public int getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(int roomNo) {
        this.roomNo = roomNo;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public String getBelongGroup() {
        return belongGroup;
    }

    public void setBelongGroup(String belongGroup) {
        this.belongGroup = belongGroup;
    }

    public boolean isReserve() {
        return reserve;
    }

    public void setReserve(boolean reserve) {
        this.reserve = reserve;
    }

    public List<SubRoom> getSubRooms() {
        return subRooms;
    }

    public void setSubRooms(List<SubRoom> subRooms) {
        this.subRooms = subRooms;
    }
}
