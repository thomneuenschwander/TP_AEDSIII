package domain;

import java.io.IOException;
import java.util.List;

import domain.exceptions.ResourceNotFoundException;

/*
    Responsibilities:

    1) Business Logic Abstraction
    2) Data Operation Orchestration
    
*/
public interface RestaurantService {

    List<Restaurant> readAll() throws Exception;

    Restaurant read(int id) throws ResourceNotFoundException, IOException;

    void update(int id, Restaurant req) throws Exception;

    void delete(int id) throws IOException;

    void saveAll(List<Restaurant> restaurants) throws Exception;
}
