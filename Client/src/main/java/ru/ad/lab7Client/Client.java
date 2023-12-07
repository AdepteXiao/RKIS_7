package ru.ad.lab7Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Client implements CommandLineRunner {

  private final ClientService clientService;

  @Autowired
  public Client(ClientService clientService) {
    this.clientService = clientService;
  }

  @Override
  public void run(String... args) {
    int isAdd = 1;
    for (int i = 0; i < 10 * isAdd; i++) {
      clientService.create(
          new Bicycle(
              "newModel" + i,
              "newBrand" + i,
              "newCondition" + i,
              (i + 1) % 10 + 1,
              (i + 1) * 1000
          )
      );
    }
    System.out.println("Было внесено 10 тестовых записей");
    clientService.printAll();
    System.out.println("Фильтация по цене:");
    clientService.printFilteredByPrice(
        (float) clientService.getPrices().stream().mapToDouble(Float::doubleValue).average().orElse(1000f)
    );
    System.out.println("Изменение первого в списке объекта:");
    clientService.update(clientService.getIds().get(0),
        new Bicycle(
            "updatedModel",
            "updatedBrand",
            "updatedCondition",
            1,
            9999999
        )
        );
    clientService.printAll();
    System.out.println("Удаление первого в списке объекта:");
    clientService.delete(clientService.getIds().get(0));
    clientService.printAll();
  }
}
