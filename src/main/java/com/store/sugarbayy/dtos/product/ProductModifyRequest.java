package com.store.sugarbayy.dtos.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record ProductModifyRequest(
        @NotBlank
        String name,
        String description,
        Long categoryId,
        Long dropId,
        List<String> images,
        @Positive
        int stockQuantity,
        @Positive
        int price
) {
}
