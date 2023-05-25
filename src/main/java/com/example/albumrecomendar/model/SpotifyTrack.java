package com.example.albumrecomendar.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpotifyTrack {
    @JsonProperty("id") //this gets track id
    private String id;
    @JsonProperty("name") //this gets track name
    private String name;
    @JsonProperty("artists") //this gets artists on track
    private List<Artist> artists;
    @JsonProperty("album") //in the album class we will get the fields of the album
    private Album album;


    public SpotifyTrack(String id, String name, List<com.example.albumrecomendar.model.Artist> artists, Album album) {
        this.id = id;
        this.name = name;
        this.artists = artists;
        this.album = album;
    }

    public SpotifyTrack(){
        this.id = "No ID set";
        this.name = "No Name set";
        this.artists = null;
        this.album = null;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public Album getAlbum() {
        return album;
    }

    public void Album(Album album) {
        this.album = album;
    }
}
