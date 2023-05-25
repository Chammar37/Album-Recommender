package com.example.albumrecomendar.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//For reading Spotify API JSON response and deserializing for our needs
//i.e, getting only 1 image from response (multiple sent)
//getting the artists name, image

//public class AlbumDeserializer extends JsonDeserializer<Album> {
//
//    //this overrides @jsonproperty
//    @Override
//    public Album deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
//        //deserialize parses Json sent in JsonNode var
//        JsonNode node = p.getCodec().readTree(p);
//
//        //extract album ID and name
//        Album album = new Album();
//        album.setId(node.get("id").asText());
//        album.setTitle(node.get("name").asText());
//
//        //get list of items under 'artists' node in Json
//        JsonNode artistsNode = node.get("artists");
//        //get artist name and add to artists list
//        List<Artist> artists = new ArrayList<>();
//        for (JsonNode artistNode : artistsNode){
//            Artist artist = new Artist();
//            artist.setName(artistNode.get("name").asText());
//            artists.add(artist);
//        }
//        album.setArtist(artists);
//
//        //getting list of items under 'images' node in json
//        JsonNode imagesNode = node.get("images");
//        //getting cover images and returning the one we want
//        if(imagesNode.isArray() && imagesNode.size()>0){
//            Image coverImage = new Image();
//            coverImage.setUrl(imagesNode.get(0).get("url").asText());
//            album.setImages(coverImage);
//        }
//
//        return album;
//
//    }
//
//
//
//}
