package com.store.sugarbayy.dtos.product;

import com.store.sugarbayy.entities.ProductDrop;
import lombok.NonNull;

import java.time.Instant;

public record DropInfo(
        @NonNull Long id,
        String name,
        String description,
        String coverPhoto,
        Instant createdAt,
        Instant updatedAt
) {
    public static DropInfo fromEntity(ProductDrop productDrop) {
        return new DropInfo(
                productDrop.getId(), productDrop.getName(), productDrop.getDescription(),
                productDrop.getCoverPhoto(), productDrop.getCreatedAt(), productDrop.getUpdatedAt()
        );
    }
}
