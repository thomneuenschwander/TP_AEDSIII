package repository.mapper;

import java.io.IOException;

import domain.Restaurant;

public interface RestaurantRecordMapper {
    
    Restaurant mapToRestaurant(byte[] record) throws IOException;

    byte[] mapToRecord(Restaurant restaurant) throws IOException;

}