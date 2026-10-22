package com.architecture.solution.client.payment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
}
