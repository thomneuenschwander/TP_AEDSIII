package database.manager;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import domain.Restaurant;

public class RestaurantSerializeble {
    private final int LENGTH_STRING_FIXED;

    public RestaurantSerializeble(int LENGTH_STRING_FIXED) {
        this.LENGTH_STRING_FIXED = LENGTH_STRING_FIXED;
    }

    public Restaurant readRestaurantFromStream(RandomAccessFile raf) throws IOException {
        int id = raf.readInt();
        String name = raf.readUTF();
        short categoriesLength = raf.readShort();
        String[] categories = new String[categoriesLength];
        for (int i = 0; i < categoriesLength; i++) {
            categories[i] = raf.readUTF();
        }
        String postalCode = readFixedLengthString(raf);
        String city = raf.readUTF();
        String address = raf.readUTF();
        Double latitude = raf.readDouble();
        Double longitude = raf.readDouble();
        Long timeInMillis = raf.readLong();
        short websitesLength = raf.readShort();
        String[] websites = new String[websitesLength];
        for (int i = 0; i < websitesLength; i++) {
            websites[i] = raf.readUTF();
        }
        return new Restaurant(id, name, categories, postalCode, city, address, latitude,
        longitude, Instant.ofEpochMilli(timeInMillis), websites);
    }

    public void writeRecorInStream(DataOutput dataOutput, Restaurant restaurant) throws IOException {
        dataOutput.writeInt(restaurant.getId());
        writeUTF(dataOutput, restaurant.getName());
        
        String[] categories = restaurant.getCategories();
        dataOutput.writeShort(categories.length);
        for (String category : categories) {
            writeUTF(dataOutput, category);
        }

        writeFixedLengthString(dataOutput, restaurant.getPostalCode());
        writeUTF(dataOutput, restaurant.getCity());
        writeUTF(dataOutput, restaurant.getAddress());
        dataOutput.writeDouble(restaurant.getLatitude());
        dataOutput.writeDouble(restaurant.getLongitude());
        dataOutput.writeLong(restaurant.getDateUpdated().toEpochMilli());

        String[] websites = restaurant.getWebsites();
        dataOutput.writeShort(websites.length);
        for (String website : websites) {
            writeUTF(dataOutput, website);
        }
    }

    public short getRecordLength(Restaurant restaurant) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        writeRecorInStream(dos, restaurant);
        baos.flush();
        dos.flush();
        return (short)baos.toByteArray().length;
    }

    public void writeUTF(DataOutput dataOutput, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        dataOutput.writeShort(bytes.length);
        dataOutput.write(bytes);
    }

    public String readFixedLengthString(RandomAccessFile raf) throws IOException {
        byte[] bytes = new byte[LENGTH_STRING_FIXED];
        raf.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }

    public void writeFixedLengthString(DataOutput dataOutput, String value) throws IOException {
        byte[] bytes = new byte[LENGTH_STRING_FIXED];
        byte[] stringBytes = value.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(stringBytes, 0, bytes, 0, Math.min(stringBytes.length, LENGTH_STRING_FIXED));
        dataOutput.write(bytes);
    }
}
