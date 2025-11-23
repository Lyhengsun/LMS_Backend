package com.norton.lms_backend.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${bakong.api.base-url}")
    private String bakongApiBaseUrl;

    @Value("${bakong.api.token}")
    private String bakongApiToken;

    @Bean
    @Qualifier("verifyBakongAccountWebClient")
    WebClient verifyBakongAccountWebClient() {
        return  WebClient.builder()
                .baseUrl(bakongApiBaseUrl + "/v1/check_bakong_account")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + bakongApiToken)
                .build();
    }
}
