package com.example.albumrecomendar.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = AlbumDeserializer.class)
public class Album {

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String title;
    @JsonProperty("artists")
    private List<Artist> artists;
    private Image coverImageUrl;

    public Album (String id, String title, List<Artist> artists, Image coverImageUrl){
        this.id = id;
        this.title = title;
        this.artists = artists;
        this.coverImageUrl = coverImageUrl;
    }

    public Album(){

    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public List<Artist> getArtist (){
        return artists;
    }

    public void setArtist(List<Artist> artists){
        this.artists = artists;
    }

    public void setCoverImageUrl(Image coverImageUrl){
        this.coverImageUrl = coverImageUrl;
    }

    public String getFirstArtistName(){
        return artists != null && !artists.isEmpty() ? artists.get(0).getName() : null;
    }

    public Image getCoverImage(){
        return coverImageUrl;
    }

    public String getCoverImageUrl(){
        return coverImageUrl != null ? coverImageUrl.getUrl() : null;
    }

}
