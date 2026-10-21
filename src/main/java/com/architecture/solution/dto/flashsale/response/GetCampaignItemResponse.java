package com.architecture.solution.dto.flashsale.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCampaignItemResponse {
    private String id;

    @JsonProperty("campaign_id")
    private String campaignId;

    private String name;

    @JsonProperty("thumbnail_id")
    private String thumbnailId;

    private BigDecimal price;

    private Long stock;
}
