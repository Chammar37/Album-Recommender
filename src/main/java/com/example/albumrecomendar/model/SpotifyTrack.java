package com.example.albumrecomendar.model;

import java.util.*;

public class SpotifyTrack {
    private String id;
    private String name;
    private List<Artist> Artists;
    private Album Album;


    public SpotifyTrack(String id, String name, List<com.example.albumrecomendar.model.Artist> artists, Album album) {
        this.id = id;
        this.name = name;
        Artists = artists;
        Album = album;
    }

    public SpotifyTrack(){
        this.id = "No ID set";
        this.name = "No Name set";
        this.Artists = null;
        this.Album = null;
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
        return Artists;
    }

    public void setArtists(List<Artist> artists) {
        Artists = artists;
    }

    public Album getAlbum() {
        return Album;
    }

    public void Album(Album album) {
        Album = album;
    }
}
