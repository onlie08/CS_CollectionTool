package com.ch.cs_collectiontool.bean;

public class RequestResult {
    private int code;
    private String message;
    private CollectInfo result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CollectInfo getResult() {
        return result;
    }

    public void setResult(CollectInfo result) {
        this.result = result;
    }
}
