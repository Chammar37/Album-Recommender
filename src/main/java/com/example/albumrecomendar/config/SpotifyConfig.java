package com.example.albumrecomendar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SpotifyConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}


//    @Value("${spotify.clientId}")
//    private String clientId;
//
//    @Value("${spotify.clientSecret}")
//    private String clientSecret;

//
//    @Bean
//    public WebClient webClient() {
//        String accessToken = WebClient.create()
//                .post()
//                .uri("https://accounts.spotify.com/api/token")
//                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes()))
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
//                .retrieve()
//                .bodyToMono(SpotifyTokenResponse.class)
//                .block()
//                .getAccess_token();
//
//        return WebClient.builder()
//                .baseUrl("https://api.spotify.com")
//                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
//                .build();
//    }

