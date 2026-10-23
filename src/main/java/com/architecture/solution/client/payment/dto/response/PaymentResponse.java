package com.architecture.solution.client.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    @JsonProperty("transaction_id")
    private String transactionId;

    private String status;
}
