package com.ch.cs_collectiontool.bean;

import java.util.List;

public class Group {
    private String groupName;
    private int gid;
    private int roomNum;
    private int doorNum;
    private String remark;
    private List<Room> rooms;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(int roomNum) {
        this.roomNum = roomNum;
    }

    public int getDoorNum() {
        return doorNum;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public void setDoorNum(int doorNum) {
        this.doorNum = doorNum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }
}
