package application.resource;

import java.io.IOException;
import java.util.List;

import domain.Restaurant;
import domain.RestaurantService;
import domain.exceptions.InvalidValueException;
import domain.exceptions.ResourceNotFoundException;

/*
    Responsibilities:

    1) Handles requests and responses directly
    2) highest application module
    
*/
public class RestaurantResource {

    private final RestaurantService service;

    private final ReaderCSV readerCSV;

    public RestaurantResource(RestaurantService service, ReaderCSV readerCSV) {
        this.service = service;
        this.readerCSV = readerCSV;
    }

    public void populate() {
        try {
            var read = readerCSV.readFile();
            service.saveAll(read);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Response<Restaurant> update(int id, Restaurant updated) {
        try {
            service.update(id, updated);
            return new Response<Restaurant>(200, "OK", null);
        } catch (ResourceNotFoundException e) {
            return new Response<Restaurant>(404, e.getMessage(), null);
        }catch (InvalidValueException e) {
            return new Response<Restaurant>(400, e.getMessage(), null);
        }  catch (Exception e) {
            return new Response<Restaurant>(400, "Bad Request", null);
        } 
    }

    public Response<List<Restaurant>> readAll(){
        try {
            var restaurants = service.readAll();
            return new Response<List<Restaurant>>(200, "OK", restaurants);
        } catch (Exception e) {
            return new Response<List<Restaurant>>(400, "Bad Request", null);
        } 
    }

    public Response<Restaurant> read(int id){
        try {
            var restaurants = service.read(id);
            return new Response<Restaurant>(200, "OK", restaurants);
        } catch (Exception e) {
            return new Response<Restaurant>(400, "Bad Request", null);
        } 
    }
    public Response<Void> delete(int id){
        try {
            service.delete(id);
            return new Response<Void>(204, "No Content", null);
        } catch (Exception e) {
            return new Response<Void>(400, "Bad Request", null);
        } 
    }
}
