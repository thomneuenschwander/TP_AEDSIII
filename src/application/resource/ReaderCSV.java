package application.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import domain.Restaurant;


/*
    Responsibilities:

    1) Read Restaurant CSV file
    
*/
public class ReaderCSV {

    private final File file;

    public ReaderCSV(String filePath) {
        this.file = new File(filePath);
    }

    public String getFilePath() {
        return file.getPath();
    }

    public List<Restaurant> readFile() throws Exception {
        List<Restaurant> restaurants = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                
                var restaurant = mapToRestaurant(line);
                restaurants.add(restaurant);
            }
        }
        return restaurants;
    }

    private Restaurant mapToRestaurant(String line) throws Exception {
        String[] splitted = line.split(",");
        int id = Integer.parseInt(splitted[0]);
        String name = splitted[1];
        String[] categories = splitted[2].split(" and ");
        String postalCode = splitted[3];
        String city = splitted[4];
        String address = splitted[5];
        Double latitude = Double.parseDouble(splitted[6].replace(".", ""));
        Double longitude = Double.parseDouble(splitted[7].replace(".", ""));
        Instant dateUpdated = Instant.parse(splitted[8]);
        String[] websites = splitted[9].split(" and ");
        return new Restaurant(id, name, categories, postalCode, city, address, latitude, longitude, dateUpdated, websites); 
    }
}
