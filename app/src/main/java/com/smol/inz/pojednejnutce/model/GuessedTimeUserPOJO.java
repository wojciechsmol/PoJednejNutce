package com.smol.inz.pojednejnutce.model;

public class GuessedTimeUserPOJO {

    private String name;
    private int gussedTime;

    public GuessedTimeUserPOJO(String name, int gussedTime) {
        this.name = name;
        this.gussedTime = gussedTime;
    }

    public GuessedTimeUserPOJO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGussedTime() {
        return gussedTime;
    }

    public void setGussedTime(int gussedTime) {
        this.gussedTime = gussedTime;
    }
}
