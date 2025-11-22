package com.example.albumrecomendar.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AudioFeatures {
    @JsonProperty("energy")
    private double energy;

    @JsonProperty("danceability")
    private double danceability;

    @JsonProperty("valence")
    private double valence;

    @JsonProperty("tempo")
    private double tempo;

    @JsonProperty("acousticness")
    private double acousticness;

    @JsonProperty("instrumentalness")
    private double instrumentalness;

    public AudioFeatures() {}

    public AudioFeatures(double energy, double danceability, double valence,
                         double tempo, double acousticness, double instrumentalness) {
        this.energy = energy;
        this.danceability = danceability;
        this.valence = valence;
        this.tempo = tempo;
        this.acousticness = acousticness;
        this.instrumentalness = instrumentalness;
    }

    public double getEnergy() { return energy; }
    public void setEnergy(double energy) { this.energy = energy; }

    public double getDanceability() { return danceability; }
    public void setDanceability(double danceability) { this.danceability = danceability; }

    public double getValence() { return valence; }
    public void setValence(double valence) { this.valence = valence; }

    public double getTempo() { return tempo; }
    public void setTempo(double tempo) { this.tempo = tempo; }

    public double getAcousticness() { return acousticness; }
    public void setAcousticness(double acousticness) { this.acousticness = acousticness; }

    public double getInstrumentalness() { return instrumentalness; }
    public void setInstrumentalness(double instrumentalness) { this.instrumentalness = instrumentalness; }
}
