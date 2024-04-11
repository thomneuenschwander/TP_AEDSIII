package application;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;

import database.domain.Record;

public class RestaurantRecord implements Record {

    private int id;
    private String name;
    private String[] categories;
    private String postalCode;
    private String city;
    private String address;
    private double latitude;
    private double longitude;
    private Instant dateUpdated;
    private String[] websites;
    private final int LENGTH_STRING_FIXED = 5;

    public RestaurantRecord() {
        this("", new String[] {}, "", "", "", 0.0, 0.0, Instant.now(), new String[] {});
    }

    public RestaurantRecord(String name, String[] categories, String postalCode, String city, String address,
            double latitude, double longitude, Instant dateUpdated, String[] websites) {
        this(-1, name, categories, postalCode, city, address, latitude, longitude, dateUpdated, websites);
    }

    public RestaurantRecord(int id, String name, String[] categories, String postalCode, String city, String address,
            double latitude, double longitude, Instant dateUpdated, String[] websites) {
        this.id = id;
        this.name = name;
        this.categories = categories;
        this.postalCode = postalCode;
        this.city = city;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateUpdated = dateUpdated;
        this.websites = websites;
    }

    @Override
    public int compareTo(Object o) {
        return this.getId() - ((RestaurantRecord) o).getId();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(out);
        serialize(stream);
        return out.toByteArray();
    }

    private void serialize(DataOutputStream stream) throws IOException {
        stream.writeInt(this.id);
        stream.writeUTF(this.name);
        stream.writeShort(this.categories.length);
        for (String category : this.categories) {
            stream.writeUTF(category);
        }
        byte[] postalCode = serializeFixedString(this.postalCode);
        stream.write(postalCode);
        stream.writeUTF(this.city);
        stream.writeUTF(this.address);
        stream.writeDouble(this.latitude);
        stream.writeDouble(this.longitude);
        long dateUpdated = this.dateUpdated.toEpochMilli();
        stream.writeLong(dateUpdated);
        stream.writeShort(this.websites.length);
        for (String website : this.websites) {
            stream.writeUTF(website);
        }
    }

    private byte[] serializeFixedString(String string) {
        var bytes = new byte[LENGTH_STRING_FIXED];
        var stringBytes = string.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(stringBytes, 0, bytes, 0, Math.min(stringBytes.length, LENGTH_STRING_FIXED));
        return bytes;
    }

    @Override
    public void fromByteArray(byte[] buffer) throws IOException {
        var in = new ByteArrayInputStream(buffer);
        var stream = new DataInputStream(in);
        deserialize(stream);
    }

    private void deserialize(DataInputStream stream) throws IOException {
        this.id = stream.readInt();
        this.name = stream.readUTF();
        int categoriesLength = stream.readShort();
        this.categories = new String[categoriesLength];
        for (int i = 0; i < categoriesLength; i++) {
            this.categories[i] = stream.readUTF();
        }
        byte[] postalCodeBytes = new byte[LENGTH_STRING_FIXED];
        stream.readFully(postalCodeBytes);
        this.postalCode = new String(postalCodeBytes, StandardCharsets.UTF_8).trim();
        this.city = stream.readUTF();
        this.address = stream.readUTF();
        this.latitude = stream.readDouble();
        this.longitude = stream.readDouble();
        long dateUpdatedMillis = stream.readLong();
        this.dateUpdated = Instant.ofEpochMilli(dateUpdatedMillis);
        int websitesLength = stream.readShort();
        this.websites = new String[websitesLength];
        for (int i = 0; i < websitesLength; i++) {
            this.websites[i] = stream.readUTF();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Instant getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Instant dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String[] getWebsites() {
        return websites;
    }

    public void setWebsites(String[] websites) {
        this.websites = websites;
    }

    @Override
    public String toString() {
        return "RestaurantRecord [id=" + id + ", name=" + name + ", categories=" + Arrays.toString(categories)
                + ", postalCode=" + postalCode + ", city=" + city + ", address=" + address + ", latitude=" + latitude
                + ", longitude=" + longitude + ", dateUpdated=" + dateUpdated + ", websites="
                + Arrays.toString(websites) + ", LENGTH_STRING_FIXED=" + LENGTH_STRING_FIXED + "]";
    }

}
