package com.architecture.solution.dto.order.response;

import com.architecture.solution.dto.order.common.OrderItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderResponse {
    @JsonProperty("items")
    private List<OrderItem> items;
}
