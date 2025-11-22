package com.example.albumrecomendar.service;

import com.example.albumrecomendar.model.*;
import com.example.albumrecomendar.model.AudioFeatures;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Service
public class SpotifyService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${spotify.api.client.id}")
    private String spotifyClientId;
    @Value("${spotify.api.client.secret}")
    private String spotifyClientSecret;
    @Value("${spotify.api.token.url}")
    private String spotifyTokenUrl;
    @Value("${spotify.api.search.url}")
    private String spotifySearchUrl;
    @Value("${spotify.api.recommendations.url}")
    private String spotifyRecommendationsUrl;
    @Value("${spotify.api.artists.url}")
    private String spotifyArtistsUrl;
    @Value("${spotify.api.albums.url:https://api.spotify.com/v1/albums}")
    private String spotifyAlbumsUrl;
    @Value("${spotify.api.audio-features.url:https://api.spotify.com/v1/audio-features}")
    private String spotifyAudioFeaturesUrl;

    private String accessToken;
    private Instant accessTokenTimeLeft;

//    public SpotifyService() {
//        try {
//            accessToken = getSpotifyTokenFromAPI();
//        }catch(IOException e){
//            logger.error("Error getting Spotify access token from API", e);
//        }
//    }
    @Autowired
    public SpotifyService(@Value("${spotify.api.client.id}") String clientId,
                          @Value("${spotify.api.client.secret}") String clientSecret,
                          @Value("${spotify.api.token.url}") String tokenUrl) {
        this.spotifyClientId = clientId;
        this.spotifyClientSecret = clientSecret;
        this.spotifyTokenUrl = tokenUrl;
//        try {
//            accessToken = getSpotifyTokenFromAPI();
//        }catch(IOException e){
//            logger.error("Error getting Spotify access token from API", e);
//        }
    }
//    public SpotifyService(RestTemplateBuilder restTemplateBuilder) {
//        this.restTemplate = restTemplateBuilder.build();
////        this.spotifyApiConfig = spotifyApiConfig;
//        try{
//            this.accessToken=getSpotifyTokenFromAPI();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//    }
    public String getAccessToken(){
        return this.accessToken;
    }

    public void setAccessToken(String accessToken){
        this.accessToken = accessToken;
    }

    public HttpHeaders getAuthorizationHeader(){
        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            if (accessToken == null || Instant.now().isAfter(accessTokenTimeLeft))
                accessToken = getSpotifyTokenFromAPI();

        }catch(IOException e){
            e.printStackTrace();
        }

        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }

    public String getSpotifyTokenFromAPI() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String credentials = spotifyClientId + ":" +spotifyClientSecret;
        String encodedCredentials = new String(Base64.getEncoder().encode(credentials.getBytes()));

        headers.set("Authorization", "Basic " + encodedCredentials);

        HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials", headers);
        ResponseEntity<SpotifyTokenResponse> response = restTemplate.postForEntity(spotifyTokenUrl, request, SpotifyTokenResponse.class);
        SpotifyTokenResponse spotifyTokenResponse = response.getBody();

        accessToken = spotifyTokenResponse.getAccess_token();
        accessTokenTimeLeft = Instant.now().plusSeconds(spotifyTokenResponse.getExpires_in() - 300); // Subtracting 300 seconds (5 minutes) as a buffer

        return spotifyTokenResponse.getAccess_token();
    }

// Takes into account the Spotify API response
// The Spotify API returns a nested JSON object, and you need to navigate to the correct
// node before deserializing it into the Albums object.
    public Albums searchAlbum(String query) throws IOException {
        HttpEntity<String> request = new HttpEntity<>(null, getAuthorizationHeader());
        ResponseEntity<String> response = restTemplate.exchange(spotifySearchUrl + "?q=" + query + "&limit=50" + "&type=album", HttpMethod.GET, request, String.class);
        JsonNode rootNode = objectMapper.readTree(response.getBody());
//        System.out.print("Spotify Root Search Response\nHERE\n" + rootNode);
        JsonNode albumsNode = rootNode.path("albums");
        Albums albums = objectMapper.readValue(albumsNode.toString(), Albums.class);

        System.out.print("\nSpotify Album Search Response\nHERE\n" + albums);

        return albums;
    }

    public Artist getGenre(String query) throws IOException {
        HttpEntity<String> request = new HttpEntity<>(null, getAuthorizationHeader());
        ResponseEntity<String> response = restTemplate.exchange(spotifyArtistsUrl + "?id=" + query, HttpMethod.GET, request, String.class);
        //Json fetching and filtering from request
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        JsonNode genreNode = rootNode.path("genres");
        Artist artist = objectMapper.readValue(genreNode.toString(), Artist.class );

        System.out.println("Genre found here: "+ artist);

        return artist;
    }

// does not account for the structure of the Spotify API response
//    public Albums searchAlbum(String query) throws IOException {
//        HttpHeaders headers = getAuthorizationHeader();
//        HttpEntity<String> request = new HttpEntity<>(headers);
//        ResponseEntity<String> response = restTemplate.exchange(spotifySearchUrl + "?q=" + query + "&type=album", HttpMethod.GET, request, String.class);
//        Albums albums = objectMapper.readValue(response.getBody(), Albums.class);
//        return albums;
//    }

    public List<Album> getAlbumRecommendations(String seedArtist,
                                               String seedGenres,
                                               String seedTracks,
                                               String targetEnergy,
                                               String targetDanceability,
                                               String targetValence) throws IOException {

    HttpHeaders headers = getAuthorizationHeader();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(spotifyRecommendationsUrl)
            .queryParam("seed_artists", seedArtist)
            .queryParam("seed_genres", seedGenres)
            .queryParam("seed_tracks", seedTracks)
            .queryParam("target_energy", targetEnergy)
            .queryParam("target_danceability", targetDanceability)
            .queryParam("target_valence", targetValence)
            .queryParam("limit", "50")
            .queryParam("market", "US");


    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
    //below 2 syso are the same
//    System.out.print("\nRecommendations Response getBody()from API \n" + response.getBody());
//    System.out.print("\nRecommendations Response from API \n" + response);
    SpotifyRecommendationsResponse recommendationsResponse = objectMapper.readValue(response.getBody(), SpotifyRecommendationsResponse.class);
//    System.out.print("\nFirst track in Response\nHERE\n" + recommendationsResponse.getTracks().get(0).toString());
    List<Album> albumList = new ArrayList<>();

        for (SpotifyTrack track : recommendationsResponse.getTracks()){
            String albumId = track.getAlbum().getId();
            String albumName = track.getAlbum().getTitle();
            List<Artist> artistName = track.getAlbum().getArtist();
//            System.out.println(artistName.get(0).getName());
            List<Image> coverImage = track.getAlbum().getImages();
//            String artistName = track.getAlbum().getArtist().get(0).getName();
//            String coverImageUrl = track.getAlbum().getImages();

            Album album = new Album(albumId, albumName, artistName, coverImage);
            albumList.add(album);
        }

        return albumList;
    }

    /**
     * Get tracks from an album
     */
    public List<SpotifyTrack> getAlbumTracks(String albumId) throws IOException {
        HttpEntity<String> request = new HttpEntity<>(null, getAuthorizationHeader());
        String url = spotifyAlbumsUrl + "/" + albumId + "/tracks?limit=50";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        JsonNode rootNode = objectMapper.readTree(response.getBody());
        JsonNode itemsNode = rootNode.path("items");

        List<SpotifyTrack> tracks = new ArrayList<>();
        for (JsonNode trackNode : itemsNode) {
            SpotifyTrack track = new SpotifyTrack();
            track.setId(trackNode.path("id").asText());
            track.setName(trackNode.path("name").asText());
            tracks.add(track);
        }
        return tracks;
    }

    /**
     * Get audio features for a track
     */
    public AudioFeatures getAudioFeatures(String trackId) throws IOException {
        HttpEntity<String> request = new HttpEntity<>(null, getAuthorizationHeader());
        String url = spotifyAudioFeaturesUrl + "/" + trackId;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        return objectMapper.readValue(response.getBody(), AudioFeatures.class);
    }

    /**
     * Get artist details including genres
     */
    public Artist getArtistDetails(String artistId) throws IOException {
        HttpEntity<String> request = new HttpEntity<>(null, getAuthorizationHeader());
        String url = spotifyArtistsUrl + "/" + artistId;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        return objectMapper.readValue(response.getBody(), Artist.class);
    }

    /**
     * Get album details
     */
    public Album getAlbumDetails(String albumId) throws IOException {
        HttpEntity<String> request = new HttpEntity<>(null, getAuthorizationHeader());
        String url = spotifyAlbumsUrl + "/" + albumId;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        return objectMapper.readValue(response.getBody(), Album.class);
    }

    /**
     * Find sonically similar albums based on an album ID
     * Uses the album's tracks, artist, and audio features to generate recommendations
     */
    public List<Album> findSimilarAlbums(String albumId) throws IOException {
        // 1. Get album details to get artist info
        Album album = getAlbumDetails(albumId);
        String artistId = album.getArtist() != null && !album.getArtist().isEmpty()
            ? album.getArtist().get(0).getId()
            : null;

        // 2. Get tracks from the album to use as seed and for audio features
        List<SpotifyTrack> tracks = getAlbumTracks(albumId);
        if (tracks.isEmpty()) {
            return new ArrayList<>();
        }

        // Use first track as seed
        String seedTrackId = tracks.get(0).getId();

        // 3. Get audio features from the first track to match sonic characteristics
        AudioFeatures features = getAudioFeatures(seedTrackId);

        // 4. Get artist genres
        String seedGenre = "";
        if (artistId != null) {
            Artist artist = getArtistDetails(artistId);
            if (artist.getGenres() != null && !artist.getGenres().isEmpty()) {
                seedGenre = artist.getGenres().get(0);
            }
        }

        // 5. Call recommendations with extracted seeds and target audio features
        return getAlbumRecommendations(
            artistId != null ? artistId : "",
            seedGenre,
            seedTrackId,
            String.valueOf(features.getEnergy()),
            String.valueOf(features.getDanceability()),
            String.valueOf(features.getValence())
        );
    }

    /**
     * Find similar albums with deduplication
     */
    public List<Album> findSimilarAlbumsDeduped(String albumId) throws IOException {
        List<Album> recommendations = findSimilarAlbums(albumId);

        // Deduplicate by album ID
        Set<String> seenIds = new HashSet<>();
        List<Album> dedupedList = new ArrayList<>();

        for (Album album : recommendations) {
            if (album.getId() != null && !seenIds.contains(album.getId())
                && !album.getId().equals(albumId)) { // Also exclude the source album
                seenIds.add(album.getId());
                dedupedList.add(album);
            }
        }

        return dedupedList;
    }


////                ALTERNATIVE solution to creating headers/endpoint url               ///////

//
//    Map<String, String> params = new HashMap<>();
//        params.put("seed_artists", seedArtist);
//        params.put("seed_genres", seedGenres);
//        params.put("seed_tracks", seedTracks);
//        params.put("limit", "20");
//        params.put("market", "US");
//        params.put("target_energy", targetEnergy);
//        params.put("target_danceability", targetDanceability);
//        params.put("target_valence", targetValence);
//
//
//    HttpHeaders headers = getAuthorizationHeader();
//    headers.setContentType(MediaType.APPLICATION_JSON);
//    HttpEntity<Map<String, String>> entity = new HttpEntity<>(params, headers);
//
//    ResponseEntity<String> response = restTemplate.exchange(spotifyRecommendationsUrl, HttpMethod.GET, entity, String.class);
//    SpotifyRecommendationsResponse recommendationsResponse = objectMapper.readValue(response.getBody(), SpotifyRecommendationsResponse.class);
//
//    List<Album> albumList = new ArrayList<>();
















    // did not use
//    public ResponseEntity<Album> searchAlbum(String query) {
//        String accessToken = getAccessToken();
//        WebClient webClient = WebClient.create();
//
//        UriComponentsBuilder searchUriBuilder = UriComponentsBuilder.fromHttpUrl("https://api.spotify.com/v1/search")
//                .queryParam("q", query)
//                .queryParam("type", "album")
//                .queryParam("limit", 1);
//
//        String searchUrl = searchUriBuilder.build(false).toUriString();
//
//        WebClient.ResponseSpec searchResponse = webClient.get()
//                .uri(searchUrl)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
//                .retrieve();
//
//        Mono<SpotifySearchResponse> searchResultMono = searchResponse.bodyToMono(SpotifySearchResponse.class);
//        SpotifySearchResponse searchResult = searchResultMono.block();
//
//        if (searchResult != null && !searchResult.getAlbums().getItems().isEmpty()) {
//            SpotifyAlbum spotifyAlbum = searchResult.getAlbums().getItems().get(0);
//            Album album = new Album();
//            album.setTitle(spotifyAlbum.getName());
//            album.setArtist(spotifyAlbum.getArtists().get(0).getName());
//            album.setImages(spotifyAlbum.getImages().get(0).getUrl());
//            album.setGenres(spotifyAlbum.getGenres());
//            return ResponseEntity.ok(album);
//        }
//
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//    }

    //worked
//    public Mono<SpotifyRecommendationsResponse> getRecommendations(String seedArtists, String seedGenres, String accessToken) {
//        WebClient webClient = WebClient.create();
//
//        return webClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .scheme("https")
//                        .host("api.spotify.com")
//                        .path("/v1/recommendations")
//                        .queryParam("seed_artists", seedArtists)
//                        .queryParam("seed_genres", seedGenres)
//                        .queryParam("limit", 20)
//                        .build())
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
//                .retrieve()
//                .bodyToMono(SpotifyRecommendationsResponse.class);
//    }

        //worked
//    public String searchAlbum(String query) {
//        UriComponentsBuilder searchUriBuilder = UriComponentsBuilder.fromHttpUrl("/search")
//                .queryParam("q", query)
//                .queryParam("type", "album")
//                .queryParam("limit", 1);
//
//        String searchUrl = searchUriBuilder.build(false).toUriString();
//
//        WebClient.ResponseSpec searchResponse = webClient.get()
//                .uri(searchUrl)
//                .retrieve();
//
//        SpotifySearchResponse searchResult = searchResponse.bodyToMono(SpotifySearchResponse.class).block();
//
//        String albumId = searchResult.getAlbums().getItems().get(0).getId();
//        return albumId;
//    }
//
//    String clientId = "3860c693f6bc4f3e9c3f59912ecd098c";
//    String clientSecret = "b99f5853e75e4f56988a457d81540f5b";
//
//    WebClient webClient = WebClient.create();
//
//    String accessToken = webClient.post()
//            .uri("https://accounts.spotify.com/api/token")
//            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//            .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes()))
//            .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
//            .retrieve()
//            .bodyToMono(SpotifyTokenResponse.class)
//            .block()
//            .getAccess_token();
//
//    //Search for an album
//    String query = "Madvillainy Madvillain";
//    UriComponentsBuilder searchUriBuilder = UriComponentsBuilder.fromHttpUrl("https://api.spotify.com/v1/search")
//            .queryParam("q", query)
//            .queryParam("type", "album")
//            .queryParam("limit", 1 );
//    String searchUrl = searchUriBuilder.build(false).toUriString();
//    WebClient.ResponseSpec searchResponse = webClient.get()
//            .uri(searchUrl)
//            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
//            .retrieve();
//
//    Mono<SpotifySearchResponse> searchResultMono = searchResponse.bodyToMono(SpotifySearchResponse.class);
//    SpotifySearchResponse searchResult = searchResultMono.block();
//
//    // Extract the album ID
//    String albumId = searchResult.getAlbums().getItems().get(0).getId();
//    System.out.println("Album ID: " + albumId);
}
