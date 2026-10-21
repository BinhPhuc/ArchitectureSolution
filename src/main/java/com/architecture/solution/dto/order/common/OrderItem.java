package com.architecture.solution.dto.order.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("variant_id")
    private String variantId;

    @JsonProperty("quantity")
    private Integer quantity;
}
