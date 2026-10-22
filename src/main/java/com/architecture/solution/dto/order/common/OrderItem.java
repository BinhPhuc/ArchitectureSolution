package com.architecture.solution.dto.order.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @JsonProperty("campaign_item_id")
    private String campaignItemId;

    @JsonProperty("quantity")
    private Long quantity;
}
