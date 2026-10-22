package com.architecture.solution.client.payment.impl;

import com.architecture.solution.client.payment.PaymentClient;
import com.architecture.solution.client.payment.dto.request.PaymentRequest;
import com.architecture.solution.client.payment.dto.response.PaymentResponse;
import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.common.ErrorResponse;
import com.architecture.solution.exception.WebClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class PaymentClientImpl implements PaymentClient {
    private final WebClient paymentClient;

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        ApiResponse<PaymentResponse> paymentResponse = paymentClient
                .post()
                .uri("/payments")
                .bodyValue(request)
                .header("Fault", "true")
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response
                        .bodyToMono(ErrorResponse.class)
                        .defaultIfEmpty(new ErrorResponse())
                        .map(errorResponse -> new WebClientException(HttpStatus.valueOf(errorResponse.getStatusCode()),
                                errorResponse
                                        .getMessage())))
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<PaymentResponse>>() {
                })
                .block();

        if (paymentResponse == null) {
            throw new WebClientException(HttpStatus.BAD_GATEWAY, "Payment gateway is not available");
        }

        if (paymentResponse.getData() == null) {
            throw new WebClientException(HttpStatus.valueOf(paymentResponse.getStatusCode()), paymentResponse.getMessage());
        }

        return paymentResponse.getData();
    }
}
