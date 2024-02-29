package repository.mapper;

import java.io.IOException;

import domain.Restaurant;

interface RestaurantRecordMapper {
    
    Restaurant mapToRestaurant(byte[] record) throws IOException;

    byte[] mapToRecord(Restaurant restaurant) throws IOException;

}