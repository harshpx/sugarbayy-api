package com.store.sugarbayy.dtos;

import java.time.Instant;

public record SuccessResponse<T>(boolean success, Instant timestamp, T data) {
    public SuccessResponse(T data) {
        this(true, Instant.now(), data);
    }
    public static <T>SuccessResponse <T>of(T data) {
        return new SuccessResponse<>(data);
    }
}
