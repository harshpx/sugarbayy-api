package com.store.sugarbayy.dtos.product;

import com.store.sugarbayy.dtos.common.NameObject;
import jakarta.validation.constraints.NotBlank;

public record CategoryName(
        @NotBlank(message = "Category name can not be empty")
        String name
) implements NameObject {
}
