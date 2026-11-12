package com.store.sugarbayy.dtos.product;

import com.store.sugarbayy.entities.Product;
import lombok.NonNull;

import java.time.Instant;
import java.util.List;

public record ProductInfo(
        @NonNull Long id,
        String name,
        String description,
        Long categoryId,
        Long dropId,
        List<String> images,
        int stockQuantity,
        int price,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProductInfo fromEntity(Product product) {
        return new ProductInfo(
                product.getId(), product.getName(), product.getDescription(),
                product.getCategory().getId(),  product.getDrop().getId(),
                product.getImages(), product.getStockQuantity(), product.getPrice(),
                product.getCreatedAt(), product.getUpdatedAt()
        );
    }
}