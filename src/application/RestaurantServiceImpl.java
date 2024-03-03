package application;

import java.util.List;

import domain.Restaurant;
import domain.RestaurantService;
import domain.exceptions.InvalidValueException;
import domain.exceptions.ResourceNotFoundException;
import repository.RestaurantRepository;

public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository repository;

    public RestaurantServiceImpl(RestaurantRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Restaurant> readAll() throws Exception {
        return repository.findAll();
    }

    @Override
    public Restaurant read(int id) throws Exception  {
        return repository.findById(id).orElseThrow(() -> new  ResourceNotFoundException(id));
    }

    @Override
    public void update(int id, Restaurant restaurant) throws Exception {
        if(!verifyPostalCode(restaurant.getPostalCode())){
            throw new InvalidValueException("postalCode", restaurant.getPostalCode());
        }
        restaurant.setId(id);
        repository.update(restaurant);
    }

    @Override
    public void delete(int id) throws Exception {
       repository.delete(id);
    }

    @Override
    public void saveAll(List<Restaurant> restaurants) throws Exception {
        repository.persistAll(restaurants);
    }
    
    private boolean verifyPostalCode(String postalCode){
        return postalCode.length() == 5;
    }
}
