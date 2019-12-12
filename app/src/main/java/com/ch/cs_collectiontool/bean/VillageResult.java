package com.ch.cs_collectiontool.bean;

public class VillageResult {
    private int code;
    private String message;
    private vid result;

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

    public vid getResult() {
        return result;
    }

    public void setResult(vid result) {
        this.result = result;
    }

    class vid{
        private String vid;

        public String getVid() {
            return vid;
        }

        public void setVid(String vid) {
            this.vid = vid;
        }
    }

}
