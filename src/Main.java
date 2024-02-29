import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
        List<Restaurant> mocked = createRestaurants();

        System.out.println("finding 357 -> " + repository.findById(357));
        System.out.println("deleting 357 -> ");
        repository.delete(357);
        System.out.println("finding 357 -> " + repository.findById(357));

        // System.out.println("updating 357 -> ");
        // repository.update(mocked.get(3));

        repository.close();
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