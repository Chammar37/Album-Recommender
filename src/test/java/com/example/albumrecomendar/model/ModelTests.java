package com.example.albumrecomendar.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DisplayName("Model Tests")
class ModelTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    @DisplayName("Album Model")
    class AlbumTests {

        @Test
        @DisplayName("should create album with all parameters")
        void constructor_WithAllParams_CreatesAlbum() {
            Artist artist = new Artist("a1", "Artist", Arrays.asList("rock"));
            Image image = new Image();
            image.setUrl("https://example.com/cover.jpg");

            Album album = new Album("id123", "Test Album", Arrays.asList(artist), Arrays.asList(image));

            assertThat(album.getId()).isEqualTo("id123");
            assertThat(album.getTitle()).isEqualTo("Test Album");
            assertThat(album.getArtist()).hasSize(1);
            assertThat(album.getImages()).hasSize(1);
        }

        @Test
        @DisplayName("should create empty album with default constructor")
        void defaultConstructor_CreatesEmptyAlbum() {
            Album album = new Album();

            assertThat(album.getId()).isNull();
            assertThat(album.getTitle()).isNull();
        }

        @Test
        @DisplayName("should get first artist name")
        void getFirstArtistName_WithArtists_ReturnsFirstName() {
            Artist artist = new Artist("a1", "First Artist", new ArrayList<>());
            Album album = new Album("id", "Album", Arrays.asList(artist), new ArrayList<>());

            assertThat(album.getFirstArtistName()).isEqualTo("First Artist");
        }

        @Test
        @DisplayName("should return null for first artist name when no artists")
        void getFirstArtistName_WithNoArtists_ReturnsNull() {
            Album album = new Album("id", "Album", new ArrayList<>(), new ArrayList<>());

            assertThat(album.getFirstArtistName()).isNull();
        }

        @Test
        @DisplayName("should get cover image URL")
        void getCoverImageUrl_WithImages_ReturnsFirstUrl() {
            Image image = new Image();
            image.setUrl("https://example.com/cover.jpg");
            Album album = new Album("id", "Album", new ArrayList<>(), Arrays.asList(image));

            assertThat(album.getCoverImageUrl()).isEqualTo("https://example.com/cover.jpg");
        }

        @Test
        @DisplayName("should return null for cover URL when no images")
        void getCoverImageUrl_WithNoImages_ReturnsNull() {
            Album album = new Album("id", "Album", new ArrayList<>(), null);

            assertThat(album.getCoverImageUrl()).isNull();
        }

        @Test
        @DisplayName("should serialize to JSON correctly")
        void serialization_ToJson_Works() throws Exception {
            Artist artist = new Artist("a1", "Artist", Arrays.asList("rock"));
            Image image = new Image();
            image.setUrl("https://example.com/cover.jpg");
            Album album = new Album("id123", "Test Album", Arrays.asList(artist), Arrays.asList(image));

            String json = objectMapper.writeValueAsString(album);

            assertThat(json).contains("\"id\":\"id123\"");
            assertThat(json).contains("\"name\":\"Test Album\"");
        }

        @Test
        @DisplayName("should deserialize from JSON correctly")
        void deserialization_FromJson_Works() throws Exception {
            String json = """
                {
                    "id": "album123",
                    "name": "Deserialized Album",
                    "artists": [{"id": "a1", "name": "Artist One"}],
                    "images": [{"url": "https://example.com/img.jpg"}]
                }
                """;

            Album album = objectMapper.readValue(json, Album.class);

            assertThat(album.getId()).isEqualTo("album123");
            assertThat(album.getTitle()).isEqualTo("Deserialized Album");
            assertThat(album.getArtist()).hasSize(1);
            assertThat(album.getImages()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Artist Model")
    class ArtistTests {

        @Test
        @DisplayName("should create artist with all parameters")
        void constructor_WithAllParams_CreatesArtist() {
            Artist artist = new Artist("id123", "Test Artist", Arrays.asList("rock", "indie"));

            assertThat(artist.getId()).isEqualTo("id123");
            assertThat(artist.getName()).isEqualTo("Test Artist");
            assertThat(artist.getGenres()).containsExactly("rock", "indie");
        }

        @Test
        @DisplayName("should handle null genres in constructor")
        void constructor_WithNullGenres_CreatesEmptyList() {
            Artist artist = new Artist("id123", "Test Artist", null);

            assertThat(artist.getGenres()).isNotNull();
            assertThat(artist.getGenres()).isEmpty();
        }

        @Test
        @DisplayName("should create empty artist with default constructor")
        void defaultConstructor_CreatesEmptyArtist() {
            Artist artist = new Artist();

            assertThat(artist.getGenres()).isNotNull();
            assertThat(artist.getGenres()).isEmpty();
        }

        @Test
        @DisplayName("should handle null in setGenres")
        void setGenres_WithNull_SetsEmptyList() {
            Artist artist = new Artist();
            artist.setGenres(null);

            assertThat(artist.getGenres()).isNotNull();
            assertThat(artist.getGenres()).isEmpty();
        }

        @Test
        @DisplayName("should serialize to JSON correctly")
        void serialization_ToJson_Works() throws Exception {
            Artist artist = new Artist("a1", "Test Artist", Arrays.asList("rock", "pop"));

            String json = objectMapper.writeValueAsString(artist);

            assertThat(json).contains("\"id\":\"a1\"");
            assertThat(json).contains("\"name\":\"Test Artist\"");
            assertThat(json).contains("\"genres\":[\"rock\",\"pop\"]");
        }

        @Test
        @DisplayName("should deserialize from JSON correctly")
        void deserialization_FromJson_Works() throws Exception {
            String json = """
                {
                    "id": "artist123",
                    "name": "JSON Artist",
                    "genres": ["electronic", "ambient"]
                }
                """;

            Artist artist = objectMapper.readValue(json, Artist.class);

            assertThat(artist.getId()).isEqualTo("artist123");
            assertThat(artist.getName()).isEqualTo("JSON Artist");
            assertThat(artist.getGenres()).containsExactly("electronic", "ambient");
        }
    }

    @Nested
    @DisplayName("AudioFeatures Model")
    class AudioFeaturesTests {

        @Test
        @DisplayName("should create audio features with all parameters")
        void constructor_WithAllParams_CreatesFeatures() {
            AudioFeatures features = new AudioFeatures(0.8, 0.7, 0.6, 120.0, 0.2, 0.1);

            assertThat(features.getEnergy()).isEqualTo(0.8);
            assertThat(features.getDanceability()).isEqualTo(0.7);
            assertThat(features.getValence()).isEqualTo(0.6);
            assertThat(features.getTempo()).isEqualTo(120.0);
            assertThat(features.getAcousticness()).isEqualTo(0.2);
            assertThat(features.getInstrumentalness()).isEqualTo(0.1);
        }

        @Test
        @DisplayName("should create empty audio features with default constructor")
        void defaultConstructor_CreatesEmptyFeatures() {
            AudioFeatures features = new AudioFeatures();

            assertThat(features.getEnergy()).isEqualTo(0.0);
            assertThat(features.getDanceability()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("should serialize to JSON correctly")
        void serialization_ToJson_Works() throws Exception {
            AudioFeatures features = new AudioFeatures(0.8, 0.7, 0.6, 120.0, 0.2, 0.1);

            String json = objectMapper.writeValueAsString(features);

            assertThat(json).contains("\"energy\":0.8");
            assertThat(json).contains("\"danceability\":0.7");
            assertThat(json).contains("\"valence\":0.6");
        }

        @Test
        @DisplayName("should deserialize from JSON correctly")
        void deserialization_FromJson_Works() throws Exception {
            String json = """
                {
                    "energy": 0.9,
                    "danceability": 0.85,
                    "valence": 0.75,
                    "tempo": 130.5,
                    "acousticness": 0.15,
                    "instrumentalness": 0.05
                }
                """;

            AudioFeatures features = objectMapper.readValue(json, AudioFeatures.class);

            assertThat(features.getEnergy()).isEqualTo(0.9);
            assertThat(features.getDanceability()).isEqualTo(0.85);
            assertThat(features.getValence()).isEqualTo(0.75);
            assertThat(features.getTempo()).isEqualTo(130.5);
        }
    }

    @Nested
    @DisplayName("SpotifyTrack Model")
    class SpotifyTrackTests {

        @Test
        @DisplayName("should create track with all parameters")
        void constructor_WithAllParams_CreatesTrack() {
            Artist artist = new Artist("a1", "Artist", new ArrayList<>());
            Album album = new Album("al1", "Album", Arrays.asList(artist), new ArrayList<>());
            SpotifyTrack track = new SpotifyTrack("t1", "Test Track", Arrays.asList(artist), album);

            assertThat(track.getId()).isEqualTo("t1");
            assertThat(track.getName()).isEqualTo("Test Track");
            assertThat(track.getArtists()).hasSize(1);
            assertThat(track.getAlbum()).isNotNull();
        }

        @Test
        @DisplayName("should create track with default constructor")
        void defaultConstructor_CreatesDefaultTrack() {
            SpotifyTrack track = new SpotifyTrack();

            assertThat(track.getId()).isEqualTo("No ID set");
            assertThat(track.getName()).isEqualTo("No Name set");
        }

        @Test
        @DisplayName("should serialize to JSON correctly")
        void serialization_ToJson_Works() throws Exception {
            Artist artist = new Artist("a1", "Artist", new ArrayList<>());
            Album album = new Album("al1", "Album", Arrays.asList(artist), new ArrayList<>());
            SpotifyTrack track = new SpotifyTrack("t1", "Test Track", Arrays.asList(artist), album);

            String json = objectMapper.writeValueAsString(track);

            assertThat(json).contains("\"id\":\"t1\"");
            assertThat(json).contains("\"name\":\"Test Track\"");
        }
    }

    @Nested
    @DisplayName("Image Model")
    class ImageTests {

        @Test
        @DisplayName("should set and get URL")
        void setUrl_AndGetUrl_Works() {
            Image image = new Image();
            image.setUrl("https://example.com/image.jpg");

            assertThat(image.getUrl()).isEqualTo("https://example.com/image.jpg");
        }

        @Test
        @DisplayName("should serialize to JSON correctly")
        void serialization_ToJson_Works() throws Exception {
            Image image = new Image();
            image.setUrl("https://example.com/cover.jpg");

            String json = objectMapper.writeValueAsString(image);

            assertThat(json).contains("\"url\":\"https://example.com/cover.jpg\"");
        }

        @Test
        @DisplayName("should deserialize from JSON correctly")
        void deserialization_FromJson_Works() throws Exception {
            String json = "{\"url\": \"https://example.com/test.jpg\"}";

            Image image = objectMapper.readValue(json, Image.class);

            assertThat(image.getUrl()).isEqualTo("https://example.com/test.jpg");
        }
    }

    @Nested
    @DisplayName("Albums Wrapper Model")
    class AlbumsTests {

        @Test
        @DisplayName("should set and get albums list")
        void setAlbums_AndGetAlbums_Works() {
            Albums albums = new Albums();
            Album album = new Album("id", "Album", new ArrayList<>(), new ArrayList<>());
            albums.setAlbums(Arrays.asList(album));

            assertThat(albums.getAlbums()).hasSize(1);
            assertThat(albums.getAlbums().get(0).getId()).isEqualTo("id");
        }

        @Test
        @DisplayName("should deserialize from Spotify search response")
        void deserialization_FromSpotifyResponse_Works() throws Exception {
            String json = """
                {
                    "items": [
                        {
                            "id": "album1",
                            "name": "First Album",
                            "artists": [],
                            "images": []
                        },
                        {
                            "id": "album2",
                            "name": "Second Album",
                            "artists": [],
                            "images": []
                        }
                    ]
                }
                """;

            Albums albums = objectMapper.readValue(json, Albums.class);

            assertThat(albums.getAlbums()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("SpotifyTokenResponse Model")
    class SpotifyTokenResponseTests {

        @Test
        @DisplayName("should set and get token properties")
        void settersAndGetters_Work() {
            SpotifyTokenResponse response = new SpotifyTokenResponse();
            response.setAccess_token("test_token");
            response.setToken_type("Bearer");
            response.setExpires_in(3600);
            response.setScope("user-read-private");

            assertThat(response.getAccess_token()).isEqualTo("test_token");
            assertThat(response.getToken_type()).isEqualTo("Bearer");
            assertThat(response.getExpires_in()).isEqualTo(3600);
            assertThat(response.getScope()).isEqualTo("user-read-private");
        }

        @Test
        @DisplayName("should deserialize from JSON correctly")
        void deserialization_FromJson_Works() throws Exception {
            String json = """
                {
                    "access_token": "BQDtoken123",
                    "token_type": "Bearer",
                    "expires_in": 3600,
                    "scope": "playlist-read-private"
                }
                """;

            SpotifyTokenResponse response = objectMapper.readValue(json, SpotifyTokenResponse.class);

            assertThat(response.getAccess_token()).isEqualTo("BQDtoken123");
            assertThat(response.getToken_type()).isEqualTo("Bearer");
            assertThat(response.getExpires_in()).isEqualTo(3600);
        }
    }

    @Nested
    @DisplayName("SpotifyRecommendationsResponse Model")
    class SpotifyRecommendationsResponseTests {

        @Test
        @DisplayName("should deserialize tracks and seeds")
        void deserialization_FromJson_Works() throws Exception {
            String json = """
                {
                    "tracks": [
                        {
                            "id": "track1",
                            "name": "Track One",
                            "artists": [],
                            "album": {
                                "id": "album1",
                                "name": "Album One",
                                "artists": [],
                                "images": []
                            }
                        }
                    ],
                    "seeds": [
                        {"id": "seed1", "type": "ARTIST"}
                    ]
                }
                """;

            SpotifyRecommendationsResponse response = objectMapper.readValue(json, SpotifyRecommendationsResponse.class);

            assertThat(response.getTracks()).hasSize(1);
            assertThat(response.getTracks().get(0).getId()).isEqualTo("track1");
            assertThat(response.getSeeds()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("SpotifySeeds Model")
    class SpotifySeedsTests {

        @Test
        @DisplayName("should deserialize seed data")
        void deserialization_FromJson_Works() throws Exception {
            String json = """
                {
                    "id": "rock",
                    "type": "GENRE"
                }
                """;

            SpotifySeeds seed = objectMapper.readValue(json, SpotifySeeds.class);

            assertThat(seed.getId()).isEqualTo("rock");
            assertThat(seed.getType()).isEqualTo("GENRE");
        }
    }
}
