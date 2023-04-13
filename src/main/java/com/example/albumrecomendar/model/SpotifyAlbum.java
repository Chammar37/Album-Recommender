package com.example.albumrecomendar.model;

import java.util.List;


//NOT SURE THIS CLASS...USING ALBUMS/ALBUM INSTEAD
public class SpotifyAlbum {
    private String id;
    private String name;
    private String imageURL;
    private List<Artist> Artists;


    public SpotifyAlbum(String id, String name, String imageURL, List<Artist> artists) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        Artists = artists;
    }

    public SpotifyAlbum(){
        this.id = "ID not set in SpotifyAlbum";
        this.name = "Name not set in SpotifyAlbum";
        this.imageURL = "ImageURL not set in SpotifyAlbum";
        Artists = null;
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public List<Artist> getArtists() {
        return Artists;
    }

    public void setArtists(List<Artist> artists) {
        Artists = artists;
    }
}
