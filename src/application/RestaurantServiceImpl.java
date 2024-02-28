package application;

import java.io.IOException;
import java.util.ArrayList;
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
        return new ArrayList<>();
    }

    @Override
    public Restaurant read(int id) throws ResourceNotFoundException, IOException  {
        return null;
    }

    @Override
    public void update(int id, Restaurant restaurant) throws Exception {
        if(!verifyPostalCode(restaurant.getPostalCode())){
            throw new InvalidValueException("postalCode", restaurant.getPostalCode());
        }
        
    }

    @Override
    public void delete(int id) throws IOException {
       
    }

    @Override
    public void saveAll(List<Restaurant> restaurants) throws Exception {
        repository.persistAll(restaurants);
    }
    
    private boolean verifyPostalCode(String postalCode){
        return postalCode.length() == 5;
    }
}
