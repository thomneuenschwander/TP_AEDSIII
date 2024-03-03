package repository.mapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Instant;

import domain.Restaurant;

public class RestaurantRecordMapperImpl implements RestaurantRecordMapper {

    private final int lengthStringFixed;

    public RestaurantRecordMapperImpl(int lengthStringFixed) {
        this.lengthStringFixed = lengthStringFixed;
    }

    @Override
    public byte[] mapToRecord(Restaurant restaurant) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        writeInDataStream(dos, restaurant);
        baos.flush();
        dos.flush();
        return baos.toByteArray();
    }

    @Override
    public Restaurant mapToRestaurant(byte[] record) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(record);
        DataInputStream dis = new DataInputStream(bais);
        var restaurant = readDataStream(dis);
        return restaurant;
    }

    private void writeInDataStream(DataOutput dataOutput, Restaurant restaurant) throws IOException {
        dataOutput.writeInt(restaurant.getId());
        dataOutput.writeUTF(restaurant.getName());
        String[] categories = restaurant.getCategories();
        dataOutput.writeShort(categories.length);
        for (String category : categories) {
            dataOutput.writeUTF(category);
        }
        writeFixedLengthString(dataOutput, restaurant.getPostalCode());
        dataOutput.writeUTF(restaurant.getCity());
        dataOutput.writeUTF(restaurant.getAddress());
        dataOutput.writeDouble(restaurant.getLatitude());
        dataOutput.writeDouble(restaurant.getLongitude());
        long timeInMillis = restaurant.getDateUpdated().toEpochMilli();
        dataOutput.writeLong(timeInMillis);
        String[] websites = restaurant.getWebsites();
        dataOutput.writeShort(websites.length);
        for (String website : websites) {
            dataOutput.writeUTF(website);
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
        String postalCode = readFixedLengthString(dis);
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

    private void writeFixedLengthString(DataOutput dataOutput, String str) throws IOException {
        byte[] bytes = new byte[lengthStringFixed];
        byte[] strBytes = str.getBytes("UTF-8");
        System.arraycopy(strBytes, 0, bytes, 0, Math.min(strBytes.length, lengthStringFixed));
        dataOutput.write(bytes);
    }

    public String readFixedLengthString(DataInputStream dis) throws IOException {
        byte[] bytes = new byte[lengthStringFixed];
        dis.readFully(bytes);
        return new String(bytes, "UTF-8").trim();
    }
}
