package com.example.albumrecomendar.model;
import java.util.List;

public class Item {
    private String id;
    private String name;
    private List<Artist> artists;

    public Item(String id, String name, List<Artist> artists){
        this.id = id;
        this.name = name;
        this.artists = artists;
    }

    public Item(){
        this.id = "";
        this.name = "";
        this.artists = null;
    }

    public void setId(String I){
        this.id = I;
    }
    public String getId(){
        return this.id;
    }

    public void setName(String n){
        this.name = n;
    }

    public String getName(){
        return this.name;
    }

    public void setArtists(List<Artist> artists_list){
        this.artists = artists_list;
    }

    public List<Artist> getArtists(){
        return this.artists;
    }
}
