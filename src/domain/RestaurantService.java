package domain;

import java.util.List;

/*
    Responsibilities:

    1) Business Logic Abstraction
    2) Data Operation Orchestration
    
*/
public interface RestaurantService {

    List<Restaurant> readAll() throws Exception;

    Restaurant read(int id) throws Exception;

    void update(int id, Restaurant req) throws Exception;

    void delete(int id) throws Exception;

    void saveAll(List<Restaurant> restaurants) throws Exception;
}
