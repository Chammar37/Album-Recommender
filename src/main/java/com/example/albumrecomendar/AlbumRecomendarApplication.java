package com.example.albumrecomendar;


import java.lang.System.*;
import com.example.albumrecomendar.model.SpotifySearchResponse;
import com.example.albumrecomendar.model.SpotifyTokenResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import java.util.Base64;

@SpringBootApplication
public class AlbumRecomendarApplication {
	public static void main(String[] args) {
//		Logger logger = LoggerFactory.getLogger(AlbumRecomendarApplication.class);
//
//		// Create a WebClient instance with logging and error handling enabled
//		WebClient webClient_first = WebClient.builder()
//				.filter(ExchangeFilterFunction.ofRequestProcessor(
//						clientRequest -> {
//							logger.info("Request: {} {}", clientRequest.method(), clientRequest.url());
//							clientRequest.headers().forEach((name, values) -> values.forEach(value -> logger.info("{}: {}", name, value)));
//							return Mono.just(clientRequest);
//						}))
//				.filter(ExchangeFilterFunction.ofResponseProcessor(
//						clientResponse -> {
//							if (clientResponse.statusCode().isError()) {
//								logger.error("Response: {}", clientResponse.statusCode());
//								return clientResponse.bodyToMono(String.class)
//										.doOnNext(body -> logger.error("Error body: {}", body))
//										.flatMap(body -> Mono.error(new RuntimeException("Error: " + clientResponse.statusCode() + ", body: " + body)));
//							} else {
//								logger.info("Response: {}", clientResponse.statusCode());
//								clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> logger.info("{}: {}", name, value)));
//								return Mono.just(clientResponse);
//							}
//						}))
//				.build();
		SpringApplication.run(AlbumRecomendarApplication.class, args);
	}

}
