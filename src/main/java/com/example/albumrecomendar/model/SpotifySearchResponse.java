package com.example.albumrecomendar.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpotifySearchResponse {
    @JsonProperty("albums")
    private Albums albums;

    public Albums getAlbums(){
        return albums;
    }

    public void setAlbums(Albums A){
        this.albums = A;
    }

}
