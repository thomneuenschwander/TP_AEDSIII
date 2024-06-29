package database;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import application.model.Restaurant;
import database.algorithms.SequentialAcess;
import database.algorithms.extensibleHash.ExtensibleHash;
import database.algorithms.invertedList.InvertedList;
import database.domain.Repository;
import database.domain.algorithms.Index;

public class RepositoryImpl implements Repository {
    private ExtensibleHash<Index> hash;
    private SequentialAcess<Restaurant> dataFile;
    private InvertedList invertedNames;
    private InvertedList invertedCitys;

    public RepositoryImpl() throws Exception {
        dataFile = new SequentialAcess<>("data.db", Restaurant.class.getConstructor());
        hash = new ExtensibleHash<>(20000, Index.class.getConstructor(), "buckets.db", "directory.db");
        List<String> nameTerms = new ArrayList<>();
        nameTerms.add("Taco Bell");
        this.invertedNames = new InvertedList("name_index.db", "name_data.db", nameTerms);

        List<String> cityTerms = new ArrayList<>();
        cityTerms.add("Orlando");
        cityTerms.add("Atlanta");
        cityTerms.add("Vancouver");
        cityTerms.add("Thibodaux");
        this.invertedCitys = new InvertedList("city_index.db", "city_data.db", cityTerms);
    }

    public void save(Restaurant restaurant) {
        try {
            Index index = dataFile.save(restaurant);
            hash.save(index);
            int id = index.getId();
            invertedCitys.insert(restaurant.getCity().toUpperCase(), id);
            invertedNames.insert(restaurant.getName().toUpperCase(), id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveAll(List<Restaurant> restaurants) {
        try {
            restaurants.forEach(restaurant -> {
                save(restaurant);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Restaurant> findAll(){
        List<Restaurant> restaurants = new ArrayList<>();
        try {
            dataFile.findAll().forEach(record -> {
                restaurants.add(record);
            });
        } catch (Exception e) {
           e.printStackTrace();
        }
        return restaurants;
    }

    public Optional<Restaurant> findById(int id) {
        try {
            var index = hash.find(id);
            return dataFile.find(index.getAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Restaurant> findByName(String nameQuery) {
        String key = nameQuery.toUpperCase();
        List<Restaurant> result = new ArrayList<>();
        try {
            if (invertedNames != null) {
                var foundIDs = invertedNames.find(key);
                foundIDs.forEach(id -> {
                    try {
                        Optional<Restaurant> restaurantO = dataFile.find(id);

                        if(restaurantO.isPresent())
                            result.add(restaurantO.get());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Restaurant> findByCity(String cityQuery) {
        String key = cityQuery.toUpperCase();
        List<Restaurant> result = new ArrayList<>();
        try {
            if (invertedNames != null) {
                var foundIDs = invertedCitys.find(key);
                foundIDs.forEach(id-> {
                    try {
                        Optional<Restaurant> restaurantO = dataFile.find(hash.find(id).getAddress());

                        if(restaurantO.isPresent())
                            result.add(restaurantO.get());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
