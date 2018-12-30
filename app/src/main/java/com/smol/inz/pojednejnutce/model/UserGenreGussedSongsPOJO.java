package com.smol.inz.pojednejnutce.model;

public class UserGenreGussedSongsPOJO {

    private int guessedOverall;
    private int guessedAmateur;
    private int guessedShowerSinger;

    public UserGenreGussedSongsPOJO() {
    }

    public UserGenreGussedSongsPOJO(int guessedOverall, int guessedAmateur, int guessedShowerSinger) {
        this.guessedOverall = guessedOverall;
        this.guessedAmateur = guessedAmateur;
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
}
