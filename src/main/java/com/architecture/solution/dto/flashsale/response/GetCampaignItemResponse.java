package com.architecture.solution.dto.flashsale.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCampaignItemResponse {
    private String id;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("variant_id")
    private String variantId;

    private BigDecimal price;

    private Long stock;

    @JsonProperty("sold_quantity")
    private Long soldQuantity;

    private String productName;

    private String description;

    private Map<String, String> variants;
}
