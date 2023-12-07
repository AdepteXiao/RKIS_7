package ru.ad.lab7Client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfiguration {

  @Bean
  public WebClient webClient(@Value("http://localhost:8080") String apiHost) {
    return WebClient.builder()
        .baseUrl(apiHost)
        .filter(logRequest())
        .filter(logResponse())
        .build();
  }

  private ExchangeFilterFunction logRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
      System.out.println("Request: " + clientRequest.method() + " " + clientRequest.url());
      return Mono.just(clientRequest);
    });
  }

  private ExchangeFilterFunction logResponse() {
    return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
      System.out.println("Response status: " + clientResponse.statusCode());
      return Mono.just(clientResponse);
    });
  }
}