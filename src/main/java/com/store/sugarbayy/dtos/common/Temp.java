package com.store.sugarbayy.dtos.common;

public record Temp(Long id, String name) implements IdNameObject {
    public Long a() {
        return this.id;
    }

    public String b() {
        return this.name;
    }
}
