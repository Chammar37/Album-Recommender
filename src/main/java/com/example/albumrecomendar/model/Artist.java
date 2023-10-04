package com.example.albumrecomendar.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Artist {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("genres")
    private List<String> genres;
    public Artist (String id, String n, List<String> g){
        this.id = id;
        this.name = n;
        this.genres.addAll(g);
    }
    public Artist (){
        name = "No Artist Name or ID Set";
    }

    public void setName(String n){
        this.name = n;
    }
    public String getName(){
        return this.name;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return this.id;
    }

    public void setGenres(List<String> genres){
        this.genres.addAll(genres);
    }
    public List<String> getGenres() {
        return this.genres;
    }
}
