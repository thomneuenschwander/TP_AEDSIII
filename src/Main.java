import java.time.Instant;

import application.RestaurantRecord;
import database.algorithms.SequentialAcess;

public class Main {
    public static void main(String[] args) throws Exception {
        RestaurantRecord res = new RestaurantRecord("Restaurante A", new String[] { "Categoria A", "Categoria B" },
                "12345", "Cidade A", "Endereço A", 40.1234, -74.5678,
                Instant.now(), new String[] { "http://site-a.com", "http://site-b.com" });

        RestaurantRecord res2 = new RestaurantRecord("adassdadsA", new String[] { "Categoria A", "Categoria B" },
                "12345", "Cidade A", "Endereço A", 40.1234, -74.5678,
                Instant.now(), new String[] { "http://site-a.com", "http://site-b.com" });

        SequentialAcess<RestaurantRecord> seq = new SequentialAcess<>("dd.db",
                RestaurantRecord.class.getConstructor());

        seq.save(res);


    }
}
