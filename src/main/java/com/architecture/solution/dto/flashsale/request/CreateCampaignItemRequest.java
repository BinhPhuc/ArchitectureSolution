package com.architecture.solution.dto.flashsale.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCampaignItemRequest {
    @JsonProperty("product_id")
    @NotEmpty
    private String productId;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    @Positive
    private Long stock;
}
