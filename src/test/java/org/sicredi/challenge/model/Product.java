package org.sicredi.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    private Integer id;
    private String title;
    private String description;
    private String category;
    private Double price;
    private Double discountPercentage;
    private Double rating;
    private Integer stock;
    private List<String> tags;
    private String brand;
    private String sku;
    private String availabilityStatus;
    private Double weight;
    private Dimensions dimensions;
    private String warrantyInformation;
    private String shippingInformation;
    private List<Review> reviews;
    private String returnPolicy;
    private Integer minimumOrderQuantity;
    private List<String> images;
    private String thumbnail;
}
