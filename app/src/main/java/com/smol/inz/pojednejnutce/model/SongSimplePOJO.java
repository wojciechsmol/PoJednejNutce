package com.smol.inz.pojednejnutce.model;

public class SongSimplePOJO {
    private String id;
    private String title;

    public SongSimplePOJO() {
    }

    public SongSimplePOJO(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
