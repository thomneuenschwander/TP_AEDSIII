import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import application.RestaurantServiceImpl;
import application.resource.ReaderCSV;
import application.resource.RestaurantResource;
import domain.Restaurant;
import repository.RestaurantRepository;
import repository.manager.RestaurantPersister;
import repository.manager.RestaurantRepositoryImpl;

public class Main {

        public static void main(String[] args) throws Exception {
                var repository = new RestaurantRepositoryImpl(
                                "Fast_Food_Restaurants.bin",
                                new RestaurantPersister(5));
                var service = new RestaurantServiceImpl(repository);
                var resource = new RestaurantResource(service, new ReaderCSV("dataset/Fast_Food_Restaurants.csv"));

                Scanner sc = new Scanner(System.in);
                while (true) {
                        System.out.println("Escolha uma forma de operar: ");
                        System.out.println("Tecle '0' para popular o banco de dados");
                        System.out.println("\n1. Acesso Sequencial");
                        System.out.println("2. Acesso Indexado com Arvore B");
                        System.out.println("3. Acesso Indexado com Hash Table");
                        System.out.println("4. Acesso Indexado com Lista invertida\n");
                        System.out.println("5. Sair do programa\n");
                        System.out.print("Opção: ");
                        int option = sc.nextInt();

                        switch (option) {
                                case 0:
                                        System.out.println("Populando... ");
                                        resource.populate();
                                        break;
                                case 1:
                                        sequentialCRUD(sc, resource);
                                        break;
                                case 4:
                                        invertedListCRUD(sc, repository);
                                        break;

                                case 5:
                                        System.out.println("Saindo do programa...");
                                        sc.close();
                                        repository.close();
                                        System.exit(0);
                                        break;
                                default:
                                        System.out.println("Opção inválida. Por favor, escolha uma opção válida.");
                        }
                }
        }

        private static void sequentialCRUD(Scanner sc, RestaurantResource resource) {
                while (true) {
                        System.out.println("\nSequencial:");
                        System.out.println("1. Ler todos registros");
                        System.out.println("2. Ler");
                        System.out.println("3. Atualizar");
                        System.out.println("4. Deletar");
                        System.out.println("5. Trocar forma de operar");
                        System.out.print("Opção: ");
                        int option = sc.nextInt();
                        switch (option) {
                                case 2:
                                        System.out.println("Digite um id para ser sequencialmente buscado: ");
                                        int id = sc.nextInt();
                                        sc.nextLine();

                                        var res2 = resource.read(id);
                                        System.out.println(res2);

                                        break;
                                case 3:
                                        System.out.print("Atualize o restaurante de id: ");
                                        int updateId = sc.nextInt();
                                        sc.nextLine();

                                        Restaurant updated = createRestaurant(sc);
                                        var res3 = resource.update(updateId, updated);
                                        System.out.println(res3);

                                        break;
                                case 4:
                                        System.out.print("Delete o restaurante de id: ");
                                        int deleteId = sc.nextInt();
                                        sc.nextLine();
                                        System.out.println("Deletando...");
                                        var res4 = resource.delete(deleteId);
                                        System.out.println(res4);

                                        break;
                                case 5:
                                        return;
                                default:
                                        System.out.println("Opção inválida. Por favor, escolha uma opção válida.");
                        }
                }
        }

        private static void invertedListCRUD(Scanner sc, RestaurantRepository repository) throws Exception {
                while (true) {
                        System.out.println("\nLista de indice invertido:\n");
                        System.out.println("1. Inicializacar lista");
                        System.out.println("2. Pesquisar");
                        System.out.println("5. Trocar forma de operar");
                        System.out.print("Opção: ");
                        int option = sc.nextInt();
                        sc.nextLine();
                        switch (option) {
                                case 1:
                                        System.out.println("Inicializando... ");
                                        repository.initializeInvertedList();
                                        break;
                                case 2:
                                        System.out.println("Escreva uma pesquisa: ");
                                        String query = sc.nextLine();
                                        var res2 = repository.findByQuery(query);
                                        res2.forEach(System.out::println);
                                        break;
                                case 5:
                                        return;
                                default:
                                        System.out.println("Opção inválida. Por favor, escolha uma opção válida.");
                        }
                }
        }

        private static Restaurant createRestaurant(Scanner sc) {
                System.out.println("Enter restaurant name:");
                String name = sc.nextLine();
                System.out.println("Enter the number of categories:");
                int numOfCategories = sc.nextInt();
                sc.nextLine();
                String[] categories = new String[numOfCategories];
                for (int i = 0; i < numOfCategories; i++) {
                        System.out.println("Enter category " + (i + 1) + ":");
                        categories[i] = sc.nextLine();
                }

                System.out.println("Enter postal code:");
                String postalCode = sc.nextLine();
                System.out.println("Enter city:");
                String city = sc.nextLine();
                System.out.println("Enter address:");
                String address = sc.nextLine();
                System.out.println("Enter latitude:");
                double latitude = sc.nextDouble();
                System.out.println("Enter longitude:");
                double longitude = sc.nextDouble();
                sc.nextLine();
                System.out.println("Enter the number of websites:");
                int numOfWebsites = sc.nextInt();
                sc.nextLine();
                String[] websites = new String[numOfWebsites];
                for (int i = 0; i < numOfWebsites; i++) {
                        System.out.println("Enter website " + (i + 1) + ":");
                        websites[i] = sc.nextLine();
                }
                return new Restaurant(name, categories, postalCode, city, address, latitude, longitude, Instant.now(),
                                websites);
        }

        public static List<Restaurant> createRestaurants() {
                List<Restaurant> restaurants = new ArrayList<>();
                restaurants.add(new Restaurant("Restaurante A", new String[] { "Categoria A", "Categoria B" },
                                "12345", "Cidade A", "Endereço A", 40.1234, -74.5678,
                                Instant.now(), new String[] { "http://site-a.com", "http://site-b.com" }));

                restaurants.add(new Restaurant("Restaurante B", new String[] { "Categoria C", "Categoria D" },
                                "54321", "Cidade B", "Endereço B", 40.5678, -74.1234,
                                Instant.now(), new String[] { "http://site-c.com", "http://site-d.com" }));
                restaurants.add(new Restaurant(3, "Restaurante C", new String[] { "Categoria E", "Categoria F" },
                                "67890", "Cidade C", "Endereço C", 40.9876, -74.9876,
                                Instant.now(), new String[] { "http://site-e.com", "http://site-f.com" }));

                restaurants.add(new Restaurant(357, "Restaurante D", new String[] { "Categoria G", "Categoria H" },
                                "98765", "Cidade D", "Endereço D", 41.1234, -75.5678,
                                Instant.now(), new String[] { "http://site-g.com", "http://site-h.com" }));

                restaurants.add(new Restaurant(5, "Restaurante E", new String[] { "Categoria I", "Categoria J" },
                                "54321", "Cidade E", "Endereço E", 41.5678, -75.1234,
                                Instant.now(), new String[] { "http://site-i.com", "http://site-j.com" }));

                return restaurants;
        }
}