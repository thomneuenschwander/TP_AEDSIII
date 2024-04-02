package domain;

import java.util.List;
import java.util.Optional;

/*
    Responsibilities:

    1) Operations related to persistence in the binary file
    2) Serialize and deserialize. (map Restaurant to binary + map binary to Restaurant)
    
*/
public interface RestaurantRepository {

  void save(Restaurant restaurant) throws Exception;

  Optional<Restaurant> findById(int id) throws Exception;

  boolean update(Restaurant updatedRestaurant) throws Exception;

  void delete(int id) throws Exception;

  void persistAll(List<Restaurant> restaurants) throws Exception;

  List<Restaurant> findAll();

  void initializeDatabase(List<Restaurant> restaurants) throws Exception;

  List<Restaurant> findByName(String name) throws Exception;

  List<Restaurant> findByCity(String city) throws Exception;

  void test() throws Exception;
}
