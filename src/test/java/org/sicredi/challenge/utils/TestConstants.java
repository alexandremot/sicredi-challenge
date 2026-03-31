package org.sicredi.challenge.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestConstants {

    private static final Random RANDOM = new Random();

    private TestConstants() {}

    public static final String BASE_URL = getProperty("api.base.url", "https://dummyjson.com");

    public static final String USERNAME = getProperty("auth.username", "emilys");
    public static final String PASSWORD = getProperty("auth.password", "emilyspass");

    public static final List<Integer> VALID_PRODUCT_IDS = Arrays.asList(1, 2, 3, 4, 5, 10, 15, 20);

    public static final int PRODUCT_INVALID_ID = 99999;
    public static final int PRODUCT_ZERO_ID = 0;
    public static final int PRODUCT_NEGATIVE_ID = -1;

    public static final List<String> PRODUCT_TITLES = Arrays.asList(
            "Perfume Oil", "Classic Watch", "Leather Bag", "Running Shoes", "Smart Speaker"
    );
    public static final List<String> PRODUCT_BRANDS = Arrays.asList(
            "Impression of Acqua Di Gio", "TimeCo", "LeatherCraft", "SpeedRun", "SoundMax"
    );
    public static final List<String> PRODUCT_CATEGORIES = Arrays.asList(
            "fragrances", "accessories", "bags", "footwear", "electronics"
    );

    public static final String PRODUCT_DESCRIPTION = "Mega Discount, Impression of A...";
    public static final Double PRODUCT_PRICE = 13.0;
    public static final Double PRODUCT_DISCOUNT = 8.4;
    public static final Double PRODUCT_RATING = 4.26;
    public static final Integer PRODUCT_STOCK = 65;
    public static final String PRODUCT_THUMBNAIL = "https://i.dummyjson.com/data/products/11/thumnail.jpg";

    public static int randomValidProductId() {
        return VALID_PRODUCT_IDS.get(RANDOM.nextInt(VALID_PRODUCT_IDS.size()));
    }

    public static String randomProductTitle() {
        return PRODUCT_TITLES.get(RANDOM.nextInt(PRODUCT_TITLES.size()));
    }

    public static String randomProductBrand() {
        return PRODUCT_BRANDS.get(RANDOM.nextInt(PRODUCT_BRANDS.size()));
    }

    public static String randomProductCategory() {
        return PRODUCT_CATEGORIES.get(RANDOM.nextInt(PRODUCT_CATEGORIES.size()));
    }

    private static String getProperty(String key, String defaultValue) {
        String envVar = key.toUpperCase().replace(".", "_");
        String value = System.getenv(envVar);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        return System.getProperty(key, defaultValue);
    }
}
