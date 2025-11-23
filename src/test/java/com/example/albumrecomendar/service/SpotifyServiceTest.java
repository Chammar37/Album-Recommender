package com.example.albumrecomendar.service;

import com.example.albumrecomendar.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpotifyService Tests")
class SpotifyServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private SpotifyService spotifyService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        spotifyService = new SpotifyService("testClientId", "testClientSecret", "https://accounts.spotify.com/api/token");

        // Inject mocked dependencies
        ReflectionTestUtils.setField(spotifyService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(spotifyService, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(spotifyService, "spotifySearchUrl", "https://api.spotify.com/v1/search");
        ReflectionTestUtils.setField(spotifyService, "spotifyRecommendationsUrl", "https://api.spotify.com/v1/recommendations");
        ReflectionTestUtils.setField(spotifyService, "spotifyArtistsUrl", "https://api.spotify.com/v1/artists");
        ReflectionTestUtils.setField(spotifyService, "spotifyAlbumsUrl", "https://api.spotify.com/v1/albums");
        ReflectionTestUtils.setField(spotifyService, "spotifyAudioFeaturesUrl", "https://api.spotify.com/v1/audio-features");
    }

    private void mockTokenResponse() {
        String tokenResponse = "{\"access_token\":\"test_token\",\"token_type\":\"Bearer\",\"expires_in\":3600}";
        ResponseEntity<SpotifyTokenResponse> tokenEntity = new ResponseEntity<>(
                new SpotifyTokenResponse() {{
                    setAccess_token("test_token");
                    setExpires_in(3600);
                }},
                HttpStatus.OK
        );
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(SpotifyTokenResponse.class)))
                .thenReturn(tokenEntity);
    }

    @Nested
    @DisplayName("Token Management")
    class TokenManagement {

        @Test
        @DisplayName("should fetch and store access token")
        void getSpotifyTokenFromAPI_ShouldReturnToken() throws IOException {
            mockTokenResponse();

            String token = spotifyService.getSpotifyTokenFromAPI();

            assertThat(token).isEqualTo("test_token");
            assertThat(spotifyService.getAccessToken()).isEqualTo("test_token");
        }

        @Test
        @DisplayName("should create authorization header with bearer token")
        void getAuthorizationHeader_ShouldIncludeBearerToken() throws IOException {
            mockTokenResponse();

            HttpHeaders headers = spotifyService.getAuthorizationHeader();

            assertThat(headers.get("Authorization")).contains("Bearer test_token");
        }
    }

    @Nested
    @DisplayName("Search Album")
    class SearchAlbum {

        @Test
        @DisplayName("should return albums when search is successful")
        void searchAlbum_WithValidQuery_ReturnsAlbums() throws IOException {
            mockTokenResponse();

            String searchResponse = """
                {
                    "albums": {
                        "items": [
                            {
                                "id": "album123",
                                "name": "Test Album",
                                "artists": [{"id": "artist1", "name": "Test Artist"}],
                                "images": [{"url": "https://example.com/image.jpg"}]
                            }
                        ]
                    }
                }
                """;

            when(restTemplate.exchange(
                    contains("/search"),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(String.class)))
                    .thenReturn(new ResponseEntity<>(searchResponse, HttpStatus.OK));

            Albums result = spotifyService.searchAlbum("Test Album");

            assertThat(result).isNotNull();
            assertThat(result.getAlbums()).hasSize(1);
            assertThat(result.getAlbums().get(0).getId()).isEqualTo("album123");
            assertThat(result.getAlbums().get(0).getTitle()).isEqualTo("Test Album");
        }

        @Test
        @DisplayName("should return empty albums when no results found")
        void searchAlbum_WithNoResults_ReturnsEmptyAlbums() throws IOException {
            mockTokenResponse();

            String searchResponse = """
                {
                    "albums": {
                        "items": []
                    }
                }
                """;

            when(restTemplate.exchange(
                    contains("/search"),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(String.class)))
                    .thenReturn(new ResponseEntity<>(searchResponse, HttpStatus.OK));

            Albums result = spotifyService.searchAlbum("NonExistent");

            assertThat(result).isNotNull();
            assertThat(result.getAlbums()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Get Album Tracks")
    class GetAlbumTracks {

        @Test
        @DisplayName("should return tracks for valid album ID")
        void getAlbumTracks_WithValidAlbumId_ReturnsTracks() throws IOException {
            mockTokenResponse();

            String tracksResponse = """
                {
                    "items": [
                        {"id": "track1", "name": "Track One"},
                        {"id": "track2", "name": "Track Two"}
                    ]
                }
                """;

            when(restTemplate.exchange(
                    contains("/albums/album123/tracks"),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(String.class)))
                    .thenReturn(new ResponseEntity<>(tracksResponse, HttpStatus.OK));

            List<SpotifyTrack> result = spotifyService.getAlbumTracks("album123");

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo("track1");
            assertThat(result.get(0).getName()).isEqualTo("Track One");
            assertThat(result.get(1).getId()).isEqualTo("track2");
        }

        @Test
        @DisplayName("should return empty list for album with no tracks")
        void getAlbumTracks_WithNoTracks_ReturnsEmptyList() throws IOException {
            mockTokenResponse();

            String tracksResponse = "{\"items\": []}";

            when(restTemplate.exchange(
                    contains("/albums/"),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(String.class)))
                    .thenReturn(new ResponseEntity<>(tracksResponse, HttpStatus.OK));

            List<SpotifyTrack> result = spotifyService.getAlbumTracks("emptyAlbum");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Get Audio Features")
    class GetAudioFeatures {

        @Test
        @DisplayName("should return audio features for valid track ID")
        void getAudioFeatures_WithValidTrackId_ReturnsFeatures() throws IOException {
            mockTokenResponse();

            String audioFeaturesResponse = """
                {
                    "energy": 0.8,
                    "danceability": 0.7,
                    "valence": 0.6,
                    "tempo": 120.0,
                    "acousticness": 0.2,
                    "instrumentalness": 0.1
                }
                """;

            when(restTemplate.exchange(
                    contains("/audio-features/track123"),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(String.class)))
                    .thenReturn(new ResponseEntity<>(audioFeaturesResponse, HttpStatus.OK));

            AudioFeatures result = spotifyService.getAudioFeatures("track123");

            assertThat(result.getEnergy()).isEqualTo(0.8);
            assertThat(result.getDanceability()).isEqualTo(0.7);
            assertThat(result.getValence()).isEqualTo(0.6);
            assertThat(result.getTempo()).isEqualTo(120.0);
        }
    }

    @Nested
    @DisplayName("Get Artist Details")
    class GetArtistDetails {

        @Test
        @DisplayName("should return artist with genres")
        void getArtistDetails_WithValidArtistId_ReturnsArtist() throws IOException {
            mockTokenResponse();

            String artistResponse = """
                {
                    "id": "artist123",
                    "name": "Test Artist",
                    "genres": ["rock", "indie", "alternative"]
                }
                """;

            when(restTemplate.exchange(
                    contains("/artists/artist123"),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(String.class)))
                    .thenReturn(new ResponseEntity<>(artistResponse, HttpStatus.OK));

            Artist result = spotifyService.getArtistDetails("artist123");

            assertThat(result.getId()).isEqualTo("artist123");
            assertThat(result.getName()).isEqualTo("Test Artist");
            assertThat(result.getGenres()).containsExactly("rock", "indie", "alternative");
        }
    }

    @Nested
    @DisplayName("Get Album Details")
    class GetAlbumDetails {

        @Test
        @DisplayName("should return album details")
        void getAlbumDetails_WithValidAlbumId_ReturnsAlbum() throws IOException {
            mockTokenResponse();

            String albumResponse = """
                {
                    "id": "album123",
                    "name": "Test Album",
                    "artists": [{"id": "artist1", "name": "Test Artist"}],
                    "images": [{"url": "https://example.com/cover.jpg"}]
                }
                """;

            when(restTemplate.exchange(
                    eq("https://api.spotify.com/v1/albums/album123"),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(String.class)))
                    .thenReturn(new ResponseEntity<>(albumResponse, HttpStatus.OK));

            Album result = spotifyService.getAlbumDetails("album123");

            assertThat(result.getId()).isEqualTo("album123");
            assertThat(result.getTitle()).isEqualTo("Test Album");
        }
    }

    @Nested
    @DisplayName("Get Album Recommendations")
    class GetAlbumRecommendations {

        @Test
        @DisplayName("should return recommended albums")
        void getAlbumRecommendations_WithValidSeeds_ReturnsAlbums() throws IOException {
            mockTokenResponse();

            String recommendationsResponse = """
                {
                    "tracks": [
                        {
                            "id": "track1",
                            "name": "Recommended Track",
                            "artists": [{"id": "artist1", "name": "Artist One"}],
                            "album": {
                                "id": "recAlbum1",
                                "name": "Recommended Album",
                                "artists": [{"id": "artist1", "name": "Artist One"}],
                                "images": [{"url": "https://example.com/rec.jpg"}]
                            }
                        }
                    ],
                    "seeds": [{"id": "rock", "type": "GENRE"}]
                }
                """;

            when(restTemplate.exchange(
                    contains("/recommendations"),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(String.class)))
                    .thenReturn(new ResponseEntity<>(recommendationsResponse, HttpStatus.OK));

            List<Album> result = spotifyService.getAlbumRecommendations(
                    "artist123", "rock", "track123", "0.7", "0.6", "0.5");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo("recAlbum1");
            assertThat(result.get(0).getTitle()).isEqualTo("Recommended Album");
        }
    }

    @Nested
    @DisplayName("Find Similar Albums Deduped")
    class FindSimilarAlbumsDeduped {

        @Test
        @DisplayName("should deduplicate albums by ID")
        void findSimilarAlbumsDeduped_WithDuplicates_ReturnsUniqueAlbums() throws IOException {
            mockTokenResponse();

            // Mock album details
            String albumResponse = """
                {
                    "id": "sourceAlbum",
                    "name": "Source Album",
                    "artists": [{"id": "artist1", "name": "Artist"}],
                    "images": [{"url": "https://example.com/source.jpg"}]
                }
                """;

            // Mock album tracks
            String tracksResponse = """
                {
                    "items": [{"id": "track1", "name": "Track One"}]
                }
                """;

            // Mock audio features
            String audioFeaturesResponse = """
                {
                    "energy": 0.8,
                    "danceability": 0.7,
                    "valence": 0.6,
                    "tempo": 120.0,
                    "acousticness": 0.2,
                    "instrumentalness": 0.1
                }
                """;

            // Mock artist details
            String artistResponse = """
                {
                    "id": "artist1",
                    "name": "Artist",
                    "genres": ["rock"]
                }
                """;

            // Mock recommendations with duplicates
            String recommendationsResponse = """
                {
                    "tracks": [
                        {
                            "id": "track1",
                            "name": "Track 1",
                            "artists": [{"id": "a1", "name": "A1"}],
                            "album": {
                                "id": "album1",
                                "name": "Album One",
                                "artists": [{"id": "a1", "name": "A1"}],
                                "images": [{"url": "https://example.com/1.jpg"}]
                            }
                        },
                        {
                            "id": "track2",
                            "name": "Track 2",
                            "artists": [{"id": "a1", "name": "A1"}],
                            "album": {
                                "id": "album1",
                                "name": "Album One",
                                "artists": [{"id": "a1", "name": "A1"}],
                                "images": [{"url": "https://example.com/1.jpg"}]
                            }
                        },
                        {
                            "id": "track3",
                            "name": "Track 3",
                            "artists": [{"id": "a2", "name": "A2"}],
                            "album": {
                                "id": "album2",
                                "name": "Album Two",
                                "artists": [{"id": "a2", "name": "A2"}],
                                "images": [{"url": "https://example.com/2.jpg"}]
                            }
                        }
                    ],
                    "seeds": []
                }
                """;

            when(restTemplate.exchange(
                    eq("https://api.spotify.com/v1/albums/sourceAlbum"),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(String.class)))
                    .thenReturn(new ResponseEntity<>(albumResponse, HttpStatus.OK));

            when(restTemplate.exchange(
                    contains("/albums/sourceAlbum/tracks"),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(String.class)))
                    .thenReturn(new ResponseEntity<>(tracksResponse, HttpStatus.OK));

            when(restTemplate.exchange(
                    contains("/audio-features/"),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(String.class)))
                    .thenReturn(new ResponseEntity<>(audioFeaturesResponse, HttpStatus.OK));

            when(restTemplate.exchange(
                    contains("/artists/"),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(String.class)))
                    .thenReturn(new ResponseEntity<>(artistResponse, HttpStatus.OK));

            when(restTemplate.exchange(
                    contains("/recommendations"),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(String.class)))
                    .thenReturn(new ResponseEntity<>(recommendationsResponse, HttpStatus.OK));

            List<Album> result = spotifyService.findSimilarAlbumsDeduped("sourceAlbum");

            // Should have 2 unique albums (album1 deduplicated, sourceAlbum excluded)
            assertThat(result).hasSize(2);
            assertThat(result).extracting(Album::getId)
                    .containsExactlyInAnyOrder("album1", "album2");
        }

        @Test
        @DisplayName("should exclude source album from results")
        void findSimilarAlbumsDeduped_ExcludesSourceAlbum() throws IOException {
            mockTokenResponse();

            String albumResponse = """
                {
                    "id": "sourceAlbum",
                    "name": "Source Album",
                    "artists": [{"id": "artist1", "name": "Artist"}],
                    "images": []
                }
                """;

            String tracksResponse = "{\"items\": [{\"id\": \"track1\", \"name\": \"Track\"}]}";

            String audioFeaturesResponse = """
                {"energy": 0.5, "danceability": 0.5, "valence": 0.5, "tempo": 100, "acousticness": 0.5, "instrumentalness": 0.5}
                """;

            String artistResponse = "{\"id\": \"artist1\", \"name\": \"Artist\", \"genres\": [\"rock\"]}";

            // Recommendations include the source album
            String recommendationsResponse = """
                {
                    "tracks": [
                        {
                            "id": "t1",
                            "name": "T1",
                            "artists": [],
                            "album": {
                                "id": "sourceAlbum",
                                "name": "Source Album",
                                "artists": [],
                                "images": []
                            }
                        },
                        {
                            "id": "t2",
                            "name": "T2",
                            "artists": [],
                            "album": {
                                "id": "otherAlbum",
                                "name": "Other Album",
                                "artists": [],
                                "images": []
                            }
                        }
                    ],
                    "seeds": []
                }
                """;

            when(restTemplate.exchange(eq("https://api.spotify.com/v1/albums/sourceAlbum"), eq(HttpMethod.GET), any(), eq(String.class)))
                    .thenReturn(new ResponseEntity<>(albumResponse, HttpStatus.OK));
            when(restTemplate.exchange(contains("/tracks"), eq(HttpMethod.GET), any(), eq(String.class)))
                    .thenReturn(new ResponseEntity<>(tracksResponse, HttpStatus.OK));
            when(restTemplate.exchange(contains("/audio-features/"), eq(HttpMethod.GET), any(), eq(String.class)))
                    .thenReturn(new ResponseEntity<>(audioFeaturesResponse, HttpStatus.OK));
            when(restTemplate.exchange(contains("/artists/"), eq(HttpMethod.GET), any(), eq(String.class)))
                    .thenReturn(new ResponseEntity<>(artistResponse, HttpStatus.OK));
            when(restTemplate.exchange(contains("/recommendations"), eq(HttpMethod.GET), any(), eq(String.class)))
                    .thenReturn(new ResponseEntity<>(recommendationsResponse, HttpStatus.OK));

            List<Album> result = spotifyService.findSimilarAlbumsDeduped("sourceAlbum");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo("otherAlbum");
        }
    }
}
