package com.architecture.solution.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {
    @Value("${payment.gateway-url}")
    private String paymentGatewayBaseUrl;

    @Value("${payment.timeout}")
    private Duration paymentTimeout;

    @Bean
    public WebClient paymentClient() {
        HttpClient httpClient = HttpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(paymentTimeout)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        return WebClient
                .builder()
                .baseUrl(paymentGatewayBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
