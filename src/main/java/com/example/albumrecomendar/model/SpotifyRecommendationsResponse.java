package com.example.albumrecomendar.model;

import java.util.List;

public class SpotifyRecommendationsResponse {

    private List<SpotifyTrack> tracks;
    private List<SpotifySeeds> seeds;
    public SpotifyRecommendationsResponse(){
        this.tracks = null;
        this.seeds = null;
    }

    public SpotifyRecommendationsResponse (List<SpotifyTrack> tracks){
        this.tracks = tracks;
    }

    public List<SpotifyTrack> getTracks(){
        return this.tracks;
    }

    public void setTracks(List<SpotifyTrack> tracks){
        this.tracks = tracks;
    }

    public List<SpotifySeeds> getSeeds() { return seeds; }

    public void setSeeds(List<SpotifySeeds> seeds) { this.seeds = seeds; }
}

