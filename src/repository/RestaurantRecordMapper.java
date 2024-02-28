package repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Instant;

import domain.Restaurant;

public class RestaurantRecordMapper {

    public byte[] mapToRecord(Restaurant restaurant) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        writeInDataStream(dos, restaurant);
        baos.flush();
        dos.flush();
        return baos.toByteArray();
    }

    public Restaurant mapToRestaurant(byte[] record) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(record);
        DataInputStream dis = new DataInputStream(bais);
        var restaurant = readDataStream(dis);
        return restaurant;
    }
    public Restaurant mapToRestaurant(DataInput dataInput) throws IOException {
        return readData(dataInput);
    }

    private void writeInDataStream(DataOutputStream dos, Restaurant restaurant) throws IOException {
        dos.writeInt(restaurant.getId());
        dos.writeUTF(restaurant.getName());
        String[] categories = restaurant.getCategories();
        dos.writeShort(categories.length);
        for (String category : categories) {
            dos.writeUTF(category);
        }
        dos.writeUTF(restaurant.getPostalCode());
        dos.writeUTF(restaurant.getCity());
        dos.writeUTF(restaurant.getAddress());
        dos.writeDouble(restaurant.getLatitude());
        dos.writeDouble(restaurant.getLongitude());
        long timeInMillis = restaurant.getDateUpdated().toEpochMilli();
        dos.writeLong(timeInMillis);
        String[] websites = restaurant.getWebsites();
        dos.writeShort(websites.length);
        for (String website : websites) {
            dos.writeUTF(website);
        }
    }

    private Restaurant readDataStream(DataInputStream dis) throws IOException {
        int id = dis.readInt();
        String name = dis.readUTF();
        short categoriesLength = dis.readShort();
        String[] categories = new String[categoriesLength];
        for (int i = 0; i < categoriesLength; i++) {
            categories[i] = dis.readUTF();
        }
        String postalCode = dis.readUTF();
        String city = dis.readUTF();
        String address = dis.readUTF();
        Double latitude = dis.readDouble();
        Double longitude = dis.readDouble();
        Long timeInMillis = dis.readLong();
        short websitesLength = dis.readShort();
        String[] websites = new String[websitesLength];
        for (int i = 0; i < websitesLength; i++) {
            websites[i] = dis.readUTF();
        }
        return new Restaurant(id, name, categories, postalCode, city, address, latitude,
                longitude, Instant.ofEpochMilli(timeInMillis), websites);
    }
    private Restaurant readData(DataInput di) throws IOException {
        int id = di.readInt();
        String name = di.readUTF();
        short categoriesLength = di.readShort();
        String[] categories = new String[categoriesLength];
        for (int i = 0; i < categoriesLength; i++) {
            categories[i] = di.readUTF();
        }
        String postalCode = di.readUTF();
        String city = di.readUTF();
        String address = di.readUTF();
        Double latitude = di.readDouble();
        Double longitude = di.readDouble();
        Long timeInMillis = di.readLong();
        short websitesLength = di.readShort();
        String[] websites = new String[websitesLength];
        for (int i = 0; i < websitesLength; i++) {
            websites[i] = di.readUTF();
        }
        return new Restaurant(id, name, categories, postalCode, city, address, latitude,
                longitude, Instant.ofEpochMilli(timeInMillis), websites);
    }

    public short calculateRestaurantRecordSize(Restaurant restaurant) {
        short size = (short) (Integer.BYTES + Short.BYTES + restaurant.getName().length() + Short.BYTES + Short.BYTES
                + restaurant.getPostalCode().length() + Short.BYTES + restaurant.getCity().length() + Short.BYTES
                + restaurant.getAddress().length() + Double.BYTES + Double.BYTES+Long.BYTES+Short.BYTES);
        for(String category : restaurant.getCategories()){
            size += category.length() + Short.BYTES;
        }
        for(String website : restaurant.getWebsites()){
            size += website.length() + Short.BYTES;
        }
        return size;
    }
}
