package com.example.albumrecomendar.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class SpotifySeeds {

    @JsonProperty("id") //this gets track id
    private String id;
    @JsonProperty("type") //this gets track id
    private String type;

    public SpotifySeeds(){
    }

    public SpotifySeeds(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
