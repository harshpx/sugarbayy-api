package com.store.sugarbayy.dtos.product;

import com.store.sugarbayy.dtos.common.IdNameObject;
import com.store.sugarbayy.entities.Category;
import lombok.NonNull;

public record CategoryInfo(@NonNull Long id, @NonNull String name) implements IdNameObject {
    public static CategoryInfo fromEntity(Category category) {
        return new CategoryInfo(category.getId(), category.getName());
    }
}
