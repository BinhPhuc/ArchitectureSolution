package com.architecture.solution.dto.flashsale.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCampaignRequest {
    @NotEmpty
    private String name;

    private String description;

    @JsonProperty("start_at")
    @NotNull
    private Instant startAt;

    @JsonProperty("end_at")
    @NotNull
    private Instant endAt;

    @NotEmpty
    private List<CreateCampaignItemRequest> items;
}
