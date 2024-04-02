package database;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import database.manager.SequentialAcess;
import database.manager.indexer.invertedList.InvertedIndex;
import domain.Restaurant;
import domain.RestaurantRepository;

public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final SequentialAcess dataFile;
    private final InvertedIndex invertedCitys;
    private final InvertedIndex invertedNames;

    public RestaurantRepositoryImpl() throws Exception {
        this.dataFile = new SequentialAcess("db.bin");
        this.invertedCitys = new InvertedIndex("index_" + "city" + ".bin", "data_" + "city" + ".bin");
        this.invertedNames = new InvertedIndex("index_" + "name" + ".bin", "data_" + "name" + ".bin");
    }

    @Override
    public void save(Restaurant restaurant) throws Exception {
        // TODO mudar o tipo de retorno para boolean se for -1
        dataFile.save(restaurant);
    }

    @Override
    public Optional<Restaurant> findById(int id) throws Exception {
        return dataFile.findById(id);
    }

    @Override
    public boolean update(Restaurant updatedRestaurant) throws Exception {
        return dataFile.update(updatedRestaurant);
    }

    @Override
    public void delete(int id) throws Exception {
        dataFile.delete(id);
    }

    @Override
    public void persistAll(List<Restaurant> restaurants) throws Exception {
        dataFile.persistAll(restaurants);
    }

    @Override
    public List<Restaurant> findAll() {
        return dataFile.findAll();
    }

    @Override
    public void initializeDatabase(List<Restaurant> restaurants) throws Exception {
        List<String> cityTerms = new ArrayList<>();
        cityTerms.add("Orlando");
        cityTerms.add("Atlanta");
        cityTerms.add("Vancouver");
        cityTerms.add("Thibodaux");
        this.invertedCitys.initializeIndexes(cityTerms);

        List<String> nameTerms = new ArrayList<>();
        nameTerms.add("Taco Bell");
        this.invertedNames.initializeIndexes(nameTerms);
        restaurants.forEach(restaurant -> {
            try {
                //TODO usar dataFileOffset do metodo save
                dataFile.save(restaurant);
                invertedCitys.insert(restaurant.getCity().toUpperCase(), restaurant.getId());
                invertedNames.insert(restaurant.getName().toUpperCase(), restaurant.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public List<Restaurant> findByName(String name) throws Exception {
        String key = name.toUpperCase();
        List<Restaurant> result = new ArrayList<>();
        if (invertedNames != null) {
            var foundID = invertedNames.find(key);
            foundID.forEach(x -> {
                try {
                    Restaurant restaurant = findById(x).get();
                    result.add(restaurant);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return result;
    }

    @Override
    public List<Restaurant> findByCity(String city) throws Exception {
        String key = city.toUpperCase();
        List<Restaurant> result = new ArrayList<>();
        if (invertedCitys != null) {
            var foundID = invertedCitys.find(key);
            foundID.forEach(x -> {
                try {
                    Restaurant restaurant = findById(x).get();
                    result.add(restaurant);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return result;
    }

    @Override
    public void test() throws Exception {
    }

}
