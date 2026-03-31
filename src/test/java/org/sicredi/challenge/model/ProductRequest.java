package org.sicredi.challenge.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductRequest {
    private String title;
    private String description;
    private Double price;
    private Double discountPercentage;
    private Double rating;
    private Integer stock;
    private String brand;
    private String category;
    private String thumbnail;
}