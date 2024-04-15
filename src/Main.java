import application.ReaderCSV;
import database.RepositoryImpl;

public class Main {
    public static void main(String[] args) throws Exception {
        var readerCSV = new ReaderCSV("./dataset/Fast_Food_Restaurants.csv");
        var repository = new RepositoryImpl();
        repository.saveAll(readerCSV.readFile());

        repository.findByName("taco bell").forEach(System.out::println);
        
        System.out.println();
        System.out.println("pesquisando com a hash");
        System.out.println(repository.findById(9));
    }
}
