package com.example.albumrecomendar.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Artist {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;

    public Artist (String id, String n){
        this.id = id;
        this.name = n;
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
}
