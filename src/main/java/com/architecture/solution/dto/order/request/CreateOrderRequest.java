package com.architecture.solution.dto.order.request;

import com.architecture.solution.dto.order.common.OrderItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    @NotEmpty
    @Valid
    @JsonProperty("items")
    private List<OrderItem> items;
}
