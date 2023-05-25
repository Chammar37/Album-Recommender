package com.example.albumrecomendar.controller;

import com.example.albumrecomendar.model.*;
import com.example.albumrecomendar.service.SpotifyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController //Can't use @RestController because every return value with @RestController is a ResponseBody, instead we use @Controller
public class AlbumController {

    @Autowired
    private final SpotifyService spotifyService;
    public AlbumController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }


    @GetMapping("/test")
    public String testThymeleaf() {
//        model.addAttribute("message", "Thymeleaf is working!");
        return "test";
    }


    @GetMapping("/search")
    public List<Album> searchAlbum(@RequestParam(value = "query") String query, Model model) {
        try {
            Albums albums = spotifyService.searchAlbum(query);
//            model.addAttribute("albums", albums.getAlbums());
            return albums.getAlbums();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @GetMapping("/recommendations")
    public List<Album> getAlbumRecommendations(@RequestParam(value = "seedArtist") String seedArtist,
                                               @RequestParam(value = "seedGenres") String seedGenres,
                                               @RequestParam(value = "seedTracks") String seedTracks,
                                               @RequestParam(value = "targetEnergy", required = false) String targetEnergy,
                                               @RequestParam(value = "targetDanceability", required = false) String targetDanceability,
                                               @RequestParam(value = "targetValence", required = false) String targetValence)
                                            throws IOException{
        try{
            List<Album> recommendations = spotifyService.getAlbumRecommendations(
                    seedArtist,
                    seedGenres,
                    seedTracks,
                    targetEnergy,
                    targetDanceability,
                    targetValence);

            return recommendations;
        } catch(IOException e){
            e.printStackTrace();
            return null;
        }

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
//        album.setImages("http://i.scdn.co/image/ab67616d0000b273163886adce4b09ec4eeea4e4");
//        return album;
//    }

//    String seedQuery = "Madvillainy Madvillain"; // Replace this with the user's input


}
