package com.store.sugarbayy.dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.store.sugarbayy.dtos.common.ListObject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CategoryIdList(
        @JsonProperty("ids")
        @NotEmpty(message = "Category ids can not be empty")
        List<@NotNull @Positive Long> list
) implements ListObject<Long> {
}
