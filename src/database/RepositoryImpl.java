package database;

import java.util.Optional;

import application.Restaurant;
import database.algorithms.SequentialAcess;
import database.algorithms.extensibleHash.ExtensibleHash;
import database.domain.Repository;
import database.domain.algorithms.Index;

public class RepositoryImpl implements Repository {

    private ExtensibleHash<Index> hash;
    private SequentialAcess<Restaurant> dataFile;

    public RepositoryImpl() throws Exception {
        this(new ExtensibleHash<>(5, Index.class.getConstructor(), "buckets.db", "directory.db"),
                new SequentialAcess<>("data.db", Restaurant.class.getConstructor()));
    }

    public RepositoryImpl(ExtensibleHash<Index> hash, SequentialAcess<Restaurant> dataFile) {
        this.hash = hash;
        this.dataFile = dataFile;
    }

    public void save(Restaurant restaurant){
        try {
            var index = dataFile.save(restaurant);
            hash.save(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Optional<Restaurant> findById(int id){
        try {
            var index = hash.find(id);
            return dataFile.find(index.getAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
