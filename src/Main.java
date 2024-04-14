import java.time.Instant;

import application.Restaurant;
import database.RepositoryImpl;

public class Main {
    public static void main(String[] args) throws Exception {
        Restaurant res = new Restaurant("Restaurante A", new String[] { "Categoria A", "Categoria B" },
                "12345", "Cidade A", "Endereço A", 40.1234, -74.5678,
                Instant.now(), new String[] { "http://site-a.com", "http://site-b.com" });

        Restaurant res2 = new Restaurant("adassdadsA", new String[] { "Categoria A", "Categoria B" },
                "12345", "Cidade A", "Endereço A", 40.1234, -74.5678,
                Instant.now(), new String[] { "http://site-a.com", "http://site-b.com" });


        RepositoryImpl rep = new RepositoryImpl();

        rep.save(res);
       

        System.out.println(rep.findById(0));
        

    }
}
