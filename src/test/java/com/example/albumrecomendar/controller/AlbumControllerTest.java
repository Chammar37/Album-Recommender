package com.example.albumrecomendar.controller;

import com.example.albumrecomendar.model.*;
import com.example.albumrecomendar.service.SpotifyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlbumController.class)
@DisplayName("AlbumController Tests")
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpotifyService spotifyService;

    @Autowired
    private ObjectMapper objectMapper;

    private Album testAlbum;
    private Artist testArtist;
    private List<Album> testAlbumList;

    @BeforeEach
    void setUp() {
        // Create test data
        testArtist = new Artist("artist123", "Test Artist", Arrays.asList("rock", "indie"));

        List<Image> images = new ArrayList<>();
        Image image = new Image();
        image.setUrl("https://example.com/album-cover.jpg");
        images.add(image);

        testAlbum = new Album("album123", "Test Album", Arrays.asList(testArtist), images);

        testAlbumList = new ArrayList<>();
        testAlbumList.add(testAlbum);
    }

    @Nested
    @DisplayName("GET /search")
    class SearchEndpoint {

        @Test
        @DisplayName("should return albums when search query is valid")
        void searchAlbum_WithValidQuery_ReturnsAlbums() throws Exception {
            Albums albums = new Albums();
            albums.setAlbums(testAlbumList);

            when(spotifyService.searchAlbum("Test Album")).thenReturn(albums);

            mockMvc.perform(get("/search")
                    .param("query", "Test Album"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is("album123")))
                    .andExpect(jsonPath("$[0].name", is("Test Album")));

            verify(spotifyService).searchAlbum("Test Album");
        }

        @Test
        @DisplayName("should return empty list when no albums found")
        void searchAlbum_WithNoResults_ReturnsEmptyList() throws Exception {
            Albums albums = new Albums();
            albums.setAlbums(new ArrayList<>());

            when(spotifyService.searchAlbum("NonExistent")).thenReturn(albums);

            mockMvc.perform(get("/search")
                    .param("query", "NonExistent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("should handle service exception gracefully")
        void searchAlbum_WhenServiceThrows_ReturnsNull() throws Exception {
            when(spotifyService.searchAlbum(anyString())).thenThrow(new IOException("API Error"));

            mockMvc.perform(get("/search")
                    .param("query", "Test"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("should require query parameter")
        void searchAlbum_WithoutQuery_ReturnsBadRequest() throws Exception {
            mockMvc.perform(get("/search"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /genre")
    class GenreEndpoint {

        @Test
        @DisplayName("should return genres for valid artist ID")
        void getGenre_WithValidArtistId_ReturnsGenres() throws Exception {
            when(spotifyService.getGenre("artist123")).thenReturn(testArtist);

            mockMvc.perform(get("/genre")
                    .param("query", "artist123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0]", is("rock")))
                    .andExpect(jsonPath("$[1]", is("indie")));

            verify(spotifyService).getGenre("artist123");
        }

        @Test
        @DisplayName("should return empty list when artist has no genres")
        void getGenre_WhenNoGenres_ReturnsEmptyList() throws Exception {
            Artist artistNoGenres = new Artist("artist456", "No Genre Artist", new ArrayList<>());
            when(spotifyService.getGenre("artist456")).thenReturn(artistNoGenres);

            mockMvc.perform(get("/genre")
                    .param("query", "artist456"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("should handle service exception gracefully")
        void getGenre_WhenServiceThrows_ReturnsNull() throws Exception {
            when(spotifyService.getGenre(anyString())).thenThrow(new IOException("API Error"));

            mockMvc.perform(get("/genre")
                    .param("query", "artist123"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /recommendations")
    class RecommendationsEndpoint {

        @Test
        @DisplayName("should return recommendations with all parameters")
        void getRecommendations_WithAllParams_ReturnsAlbums() throws Exception {
            when(spotifyService.getAlbumRecommendations(
                    anyString(), anyString(), anyString(),
                    anyString(), anyString(), anyString()))
                    .thenReturn(testAlbumList);

            mockMvc.perform(get("/recommendations")
                    .param("seedArtist", "artist123")
                    .param("seedGenres", "rock")
                    .param("seedTracks", "track123")
                    .param("targetEnergy", "0.7")
                    .param("targetDanceability", "0.6")
                    .param("targetValence", "0.5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is("album123")));

            verify(spotifyService).getAlbumRecommendations(
                    "artist123", "rock", "track123", "0.7", "0.6", "0.5");
        }

        @Test
        @DisplayName("should work with only required parameters")
        void getRecommendations_WithRequiredParamsOnly_ReturnsAlbums() throws Exception {
            when(spotifyService.getAlbumRecommendations(
                    anyString(), anyString(), anyString(),
                    any(), any(), any()))
                    .thenReturn(testAlbumList);

            mockMvc.perform(get("/recommendations")
                    .param("seedArtist", "artist123")
                    .param("seedGenres", "rock")
                    .param("seedTracks", "track123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("should return bad request when required params missing")
        void getRecommendations_WithMissingParams_ReturnsBadRequest() throws Exception {
            mockMvc.perform(get("/recommendations")
                    .param("seedArtist", "artist123"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should handle service exception gracefully")
        void getRecommendations_WhenServiceThrows_ReturnsNull() throws Exception {
            when(spotifyService.getAlbumRecommendations(
                    anyString(), anyString(), anyString(),
                    any(), any(), any()))
                    .thenThrow(new IOException("API Error"));

            mockMvc.perform(get("/recommendations")
                    .param("seedArtist", "artist123")
                    .param("seedGenres", "rock")
                    .param("seedTracks", "track123"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /similar")
    class SimilarEndpoint {

        @Test
        @DisplayName("should return similar albums for valid album ID")
        void getSimilarAlbums_WithValidAlbumId_ReturnsAlbums() throws Exception {
            when(spotifyService.findSimilarAlbumsDeduped("album123")).thenReturn(testAlbumList);

            mockMvc.perform(get("/similar")
                    .param("albumId", "album123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is("album123")))
                    .andExpect(jsonPath("$[0].name", is("Test Album")));

            verify(spotifyService).findSimilarAlbumsDeduped("album123");
        }

        @Test
        @DisplayName("should return empty list when no similar albums found")
        void getSimilarAlbums_WithNoResults_ReturnsEmptyList() throws Exception {
            when(spotifyService.findSimilarAlbumsDeduped("album456")).thenReturn(new ArrayList<>());

            mockMvc.perform(get("/similar")
                    .param("albumId", "album456"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("should return empty list when service throws exception")
        void getSimilarAlbums_WhenServiceThrows_ReturnsEmptyList() throws Exception {
            when(spotifyService.findSimilarAlbumsDeduped(anyString()))
                    .thenThrow(new IOException("API Error"));

            mockMvc.perform(get("/similar")
                    .param("albumId", "album123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("should require albumId parameter")
        void getSimilarAlbums_WithoutAlbumId_ReturnsBadRequest() throws Exception {
            mockMvc.perform(get("/similar"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /test")
    class TestEndpoint {

        @Test
        @DisplayName("should return test template name")
        void testThymeleaf_ReturnsTestTemplate() throws Exception {
            mockMvc.perform(get("/test"))
                    .andExpect(status().isOk());
        }
    }
}
