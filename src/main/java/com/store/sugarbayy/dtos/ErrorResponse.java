package com.store.sugarbayy.dtos;

import java.time.Instant;

public record ErrorResponse(boolean success, Instant timestamp, String data) {
    public ErrorResponse(String data) {
        this(false, Instant.now(), data);
    }
}
