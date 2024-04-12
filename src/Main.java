import java.time.Instant;

import application.Restaurant;
import database.algorithms.SequentialAcess;
import database.algorithms.bPlusTree.BPlusTreePK_FK;

public class Main {
    public static void main(String[] args) throws Exception {
        Restaurant res = new Restaurant("Restaurante A", new String[] { "Categoria A", "Categoria B" },
                "12345", "Cidade A", "Endereço A", 40.1234, -74.5678,
                Instant.now(), new String[] { "http://site-a.com", "http://site-b.com" });

        Restaurant res2 = new Restaurant("adassdadsA", new String[] { "Categoria A", "Categoria B" },
                "12345", "Cidade A", "Endereço A", 40.1234, -74.5678,
                Instant.now(), new String[] { "http://site-a.com", "http://site-b.com" });

        SequentialAcess<Restaurant> seq = new SequentialAcess<>("dd.db",
                Restaurant.class.getConstructor());

        BPlusTreePK_FK b = new BPlusTreePK_FK(4, "arv.db");
        b.save(7, 21);
        b.save(9, 11);
        b.save(88, 12);
        
        b.delete(88, 12);

        b.find(88).stream().forEach(System.out::println);
    }
}
