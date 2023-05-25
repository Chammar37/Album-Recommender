package com.example.albumrecomendar.model;

import java.util.List;

public class SpotifyRecommendationsResponse {
    private List<SpotifyTrack> tracks;

    public SpotifyRecommendationsResponse(){
        this.tracks = null;
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
}

