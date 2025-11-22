package com.example.albumrecomendar.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Artist {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("genres")
    private List<String> genres = new ArrayList<>();

    public Artist(String id, String name, List<String> genres) {
        this.id = id;
        this.name = name;
        this.genres = genres != null ? new ArrayList<>(genres) : new ArrayList<>();
    }

    public Artist() {
        this.genres = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres != null ? new ArrayList<>(genres) : new ArrayList<>();
    }

    public List<String> getGenres() {
        return this.genres;
    }
}
