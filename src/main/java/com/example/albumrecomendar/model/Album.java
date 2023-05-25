package com.example.albumrecomendar.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

//@JsonDeserialize(using = AlbumDeserializer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Album {

    @JsonProperty("id") //id of album
    private String id;
    @JsonProperty("name") //name of album
    private String title;
    @JsonProperty("artists") //artist that released album
    private List<Artist> artists;
    @JsonProperty("images") //
    private List<Image> images;

    public Album (String id, String title, List<Artist> artists, List<Image> images){
        this.id = id;
        this.title = title;
        this.artists = artists;
        this.images = images;
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

    public void setImages(List<Image> images){
        this.images = images;
    }

    public String getFirstArtistName(){
        return artists != null && !artists.isEmpty() ? artists.get(0).getName() : null;
    }

    public List<Image> getImages(){
        return images;
    }

    public String getCoverImageUrl(){
        return images != null ? getImages().get(0).getUrl() : null;
    }

}
