package com.norton.lms_backend.model.dto.response;

import java.time.Instant;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String message;
    private T payload;
    private HttpStatus status;
    private Integer code;
    @Builder.Default
    private Instant instant = Instant.now();
}
