package com.architecture.solution.dto.order.request;

import com.architecture.solution.dto.order.common.OrderItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    @JsonProperty("items")
    private List<OrderItem> items;
}
