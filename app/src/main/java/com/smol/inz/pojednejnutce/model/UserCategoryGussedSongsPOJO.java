package com.smol.inz.pojednejnutce.model;

public class UserCategoryGussedSongsPOJO {

    private int guessedOverall;
    private int guessedAmateur;
    private int guessedProfessional;
    private int guessedShowerSinger;

    public UserCategoryGussedSongsPOJO() {
    }

    public UserCategoryGussedSongsPOJO(int guessedOverall, int guessedAmateur, int guessedProfessional, int guessedShowerSinger) {
        this.guessedOverall = guessedOverall;
        this.guessedAmateur = guessedAmateur;
        this.guessedProfessional = guessedProfessional;
        this.guessedShowerSinger = guessedShowerSinger;
    }

    public int getGuessedOverall() {
        return guessedOverall;
    }

    public void setGuessedOverall(int guessedOverall) {
        this.guessedOverall = guessedOverall;
    }

    public int getGuessedAmateur() {
        return guessedAmateur;
    }

    public void setGuessedAmateur(int guessedAmateur) {
        this.guessedAmateur = guessedAmateur;
    }

    public int getGuessedShowerSinger() {
        return guessedShowerSinger;
    }

    public void setGuessedShowerSinger(int guessedShowerSinger) {
        this.guessedShowerSinger = guessedShowerSinger;
    }

    public int getGuessedProfessional() {
        return guessedProfessional;
    }

    public void setGuessedProfessional(int guessedProfessional) {
        this.guessedProfessional = guessedProfessional;
    }
}
