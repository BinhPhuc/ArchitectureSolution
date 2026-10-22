package com.architecture.solution.client.payment;

import com.architecture.solution.client.payment.dto.request.PaymentRequest;
import com.architecture.solution.client.payment.dto.response.PaymentResponse;

public interface PaymentClient {
    PaymentResponse processPayment(PaymentRequest request);
}
