import java.time.Instant;

import application.RestaurantServiceImpl;
import application.resource.ReaderCSV;
import application.resource.RestaurantResource;
import domain.Restaurant;
import repository.RestaurantRecordMapper;
import repository.RestaurantRepositoryImpl;

/**
 * Main
 */
public class Main {

    public static void main(String[] args) throws Exception {

        var repository = new RestaurantRepositoryImpl("src/repository/db/Fast_Food_Restaurants.bin", new RestaurantRecordMapper());
        var service = new RestaurantServiceImpl(repository);
        var resource = new RestaurantResource(service, new ReaderCSV("dataset/Fast_Food_Restaurants.csv"));

        resource.populate();

        System.out.println(repository.findById(6));
        String[] cat = {"Italiano", "Pizzaria", "Massas"};
        String[] web = {"http://www.restauranteabc.com"};
        repository.save(new Restaurant("Restaurante ABC", cat, "12345", "São Paulo", "Rua das Pizzas, 123", -23.567, -46.789, Instant.now(), web));
        System.out.println(repository.findById(10000));
        System.out.println();
        System.out.println(repository.findById(31));
        String[] cat2 = {"Churrascaria", "Brasileira", "Carnes"};
        String[] web2 = {"http://www.churrascariabrasil.com"};
        repository.update(new Restaurant(31,"Churrascaria Brasil", cat2, "54321", "Rio de Janeiro", "Avenida das Carnes, 456", -22.987, -43.215, Instant.now(), web2));
        System.out.println(repository.findById(31));
        String[] cat3 = {"Japonês", "Sushi", "Frutos do Mar"};
        String[] web3 = {"http://www.sushimaster.com"};
        repository.save(new Restaurant("Sushi Master", cat3, "98765", "Tokyo", "Shibuya-ku, 1-2-3", 35.6895, 139.6917, Instant.now(), web3));
        System.out.println(repository.findById(10001));
    }

    
}