package com.smart.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;

// TODO: name is kind of ambiguous. Replace later?
public record OrderItem(
        @JsonProperty("rid") int restaurant_id,
        @JsonProperty("id")  int product_id,
        int count) {
}
