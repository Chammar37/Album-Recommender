package com.example.albumrecomendar.controller;

import com.example.albumrecomendar.model.*;
import com.example.albumrecomendar.service.SpotifyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
public class AlbumController {

    @Autowired
    private final SpotifyService spotifyService;
    public AlbumController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/search")
    public String searchAlbum(@RequestParam(value = "query") String query, Model model) {
        try {
            Albums albums = spotifyService.searchAlbum(query);
            model.addAttribute("albums", albums.getAlbums());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "search :: searchResults";
    }


    @GetMapping("/recommendations")
    public String getAlbumRecommendations(@RequestParam("albumId") String albumId,
                                          @RequestParam("energy") String energy,
                                          @RequestParam("danceability") String danceability,
                                          @RequestParam("valence") String valence,
                                          Model model) throws IOException{
        List<Album> recommendations = spotifyService.getAlbumRecommendations(albumId, "", energy, danceability, valence);
        return "recommendations";
    }


//    @GetMapping(value = "/search")
//    public ResponseEntity<Album> searchAlbum(@RequestParam String query) {
//        Album album = spotifyService.searchAlbum(query);
//        return ResponseEntity.ok(album);
//    }


//    @GetMapping("/api/album")
//    public Album getAlbum(@RequestParam String query){
//        Album album = new Album();
//        album.setTitle("Madvillainy");
//        album.setArtist("Madvillain");
//        album.setCoverImageUrl("http://i.scdn.co/image/ab67616d0000b273163886adce4b09ec4eeea4e4");
//        return album;
//    }

//    String seedQuery = "Madvillainy Madvillain"; // Replace this with the user's input


}
