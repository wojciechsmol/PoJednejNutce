package com.smol.inz.pojednejnutce.model;

public class UserPOJO {

    private String name;
    private int score;
    private int scorePOP;
    private int scoreROCK;

    public UserPOJO() {
    }

    public UserPOJO(String name, int score, int scorePOP, int scoreROCK) {
        this.name = name;
        this.score = score;
        this.scorePOP = scorePOP;
        this.scoreROCK = scoreROCK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScorePOP() {
        return scorePOP;
    }

    public void setScorePOP(int scorePOP) {
        this.scorePOP = scorePOP;
    }

    public int getScoreROCK() {
        return scoreROCK;
    }

    public void setScoreROCK(int scoreROCK) {
        this.scoreROCK = scoreROCK;
    }
}
