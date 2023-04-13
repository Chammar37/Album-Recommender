package com.example.albumrecomendar.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Albums {
    @JsonProperty("items")
    private List<Album> albums;

    public Albums (List<Album> items){
        this.albums = items;
    }

    public Albums(){
    }
    public List<Album> getAlbums (){
        return this.albums;
    }

    public void setAlbums(List<Album> I){
        this.albums = I;
    }

}
