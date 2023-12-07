package ru.ad.lab7Client;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ClientService {

  private final WebClient webClient;

  private List<Integer> ids = new ArrayList<>();
  private List<Float> prices = new ArrayList<>();


  @Autowired
  public ClientService(WebClient webClient) {
    this.webClient = webClient;
  }

  public void printAll() {
    ids.clear();
    prices.clear();
    webClient.get()
        .uri("/api/bicycles")
        .retrieve()
        .bodyToFlux(Bicycle.class)
        .collectList()
        .doOnSuccess(
            bicycles -> System.out.println(
                "Список велосипедов:\n" +
                    bicycles.stream()
                        .map(bicycle -> {
                          ids.add(bicycle.getId());
                          prices.add(bicycle.getPrice());
                          return bicycle.toString();
                        }).collect(Collectors.joining("\n"))
            ))
        .subscribeOn(Schedulers.boundedElastic())
        .block();
  }

  public Bicycle getById(int id) {
    return webClient.get()
        .uri("/api/bicycles/" + id)
        .retrieve().bodyToMono(Bicycle.class)
        .doOnSuccess(bicycle ->
            System.out.println(String.format("Запись с id = %d ", bicycle.getId()) + bicycle)
        ).subscribeOn(Schedulers.boundedElastic())
        .block();
  }

  public void printFilteredByPrice(Float price) {
    webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/api/bicycles")
            .queryParam("price", price)
            .build())
        .retrieve()
        .bodyToFlux(Bicycle.class)
        .collectList()
        .doOnSuccess(
            bicycles -> System.out.println(
                "Список велосипедов c ценой меньше чем " + price + ":\n" +
                    bicycles.stream()
                        .map(bicycle -> {
                          ids.add(bicycle.getId());
                          prices.add(bicycle.getPrice());
                          return bicycle.toString();
                        }).collect(Collectors.joining("\n"))
            ))
        .subscribeOn(Schedulers.boundedElastic())
        .block();
  }

  public void create(Bicycle bicycle) {
    webClient.post()
        .uri("/api/bicycles")
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(bicycle), Bicycle.class)
        .retrieve()
        .bodyToMono(Void.class)
        .block();
  }

  public void update(int id, Bicycle updatedInstrument) {
    webClient.put()
        .uri("/api/bicycles/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(updatedInstrument))
        .retrieve()
        .bodyToMono(Void.class)
        .block();
  }

  public void delete(int id) {
    webClient.delete()
        .uri("/api/bicycles/{id}", id)
        .retrieve()
        .bodyToMono(Void.class)
        .block();
  }

  public List<Integer> getIds() {
    return ids;
  }

  public List<Float> getPrices() {
    return prices;
  }
}

