package com.architecture.solution.dto.order.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @NotBlank
    @JsonProperty("campaign_item_id")
    private String campaignItemId;

    @NotNull
    @Positive
    @JsonProperty("quantity")
    private Integer quantity;
}
