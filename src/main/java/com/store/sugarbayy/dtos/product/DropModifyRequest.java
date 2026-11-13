package com.store.sugarbayy.dtos.product;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record DropModifyRequest(
        @NotBlank(message = "Product drop must have a name")
        String name,
        String description,
        MultipartFile coverPhoto,
        Boolean deleteCoverPhoto
) {
}
