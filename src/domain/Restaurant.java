package domain;

import java.time.Instant;
import java.util.Arrays;

public class Restaurant {

        private int id; // Identity
        private String name; // Variable-size
        private String[] categories; // List of values
        private String postalCode; // Fixed-size
        private String city; // Variable-size
        private String address; // Variable-size
        private double latitude; // Floating point
        private double longitude; // Floating point
        private Instant dateUpdated; // Date
        private String[] websites; // List of values

        public Restaurant(String name, String[] categories, String postalCode, String city, String address,
                        double latitude, double longitude, Instant dateUpdated, String[] websites) {
                this(-1, name, categories, postalCode, city, address, latitude, longitude, dateUpdated, websites);
        }

        public Restaurant(int id, String name, String[] categories, String postalCode, String city, String address,
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

        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
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
                return "Restaurant [id=" + id + ", name=" + name + ", categories=" + Arrays.toString(categories)
                                + ", postalCode=" + postalCode + ", city=" + city + ", address=" + address
                                + ", latitude=" + latitude + ", longitude=" + longitude + ", dateUpdated=" + dateUpdated
                                + ", websites=" + Arrays.toString(websites) + "]";
        }
            
}