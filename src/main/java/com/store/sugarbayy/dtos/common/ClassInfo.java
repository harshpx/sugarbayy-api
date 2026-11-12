package com.store.sugarbayy.dtos.common;

import lombok.NonNull;

public record ClassInfo(@NonNull Long id, @NonNull String name) implements IdNameObject {
}
