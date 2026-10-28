package com.architecture.solution.dto.order.response;

import com.architecture.solution.dto.order.common.OrderItem;
import com.architecture.solution.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderResponse {
    private String id;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    private OrderStatus status;

    @JsonProperty("items")
    private List<OrderItem> items;
}
