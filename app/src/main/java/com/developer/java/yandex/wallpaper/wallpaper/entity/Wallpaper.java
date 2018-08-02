package com.developer.java.yandex.wallpaper.wallpaper.entity;

public class Wallpaper {
    private final int mId;
    private String mName;
    private String mPath;

    public Wallpaper(int mId, String mName, String mPath) {
        this.mId = mId;
        this.mName = mName;
        this.mPath = mPath;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }
}
