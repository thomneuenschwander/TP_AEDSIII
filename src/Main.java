import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import application.RestaurantServiceImpl;
import application.resource.ReaderCSV;
import application.resource.RestaurantResource;
import domain.Restaurant;
import repository.RestaurantRepositoryImpl;
import repository.mapper.RestaurantRecordMapperImpl;

/**
 * Main
 */
public class Main {

        public static void main(String[] args) throws Exception {

                var repository = new RestaurantRepositoryImpl("src/repository/db/Fast_Food_Restaurants.bin",
                                new RestaurantRecordMapperImpl(5));
                var service = new RestaurantServiceImpl(repository);
                var resource = new RestaurantResource(service, new ReaderCSV("dataset/Fast_Food_Restaurants.csv"));

                resource.populate();

                Scanner sc = new Scanner(System.in);
                while (true) {
                        loadMenu();
                        System.out.print("Opção: ");
                        int option = sc.nextInt();

                        switch (option) {
                                case 1:
                                        var res1 = resource.readAll();
                                        System.out.println(res1);

                                        break;
                                case 2:
                                        System.out.println("Digite o id para ser buscado: ");
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
                                        // Restaurant updated = createRestaurants().get(0);
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

        private static void loadMenu() {
                System.out.println("\nEscolha uma operação:");
                System.out.println("1. Ler todos registros");
                System.out.println("2. Ler");
                System.out.println("3. Atualizar");
                System.out.println("4. Deletar");
                System.out.println("5. Sair");
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