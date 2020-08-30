package com.example.eclass.recording;

public class Video {

    private String title;
    private String url;
    private String search;

    public Video(){};

    public Video(String url, String title, String search) {
        this.url = url;
        this.title = title;
        this.search = search;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getSearch() {
        return search;
    }
}
