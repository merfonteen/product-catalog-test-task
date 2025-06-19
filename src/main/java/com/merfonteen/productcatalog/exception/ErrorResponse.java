package com.merfonteen.productcatalog.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@AllArgsConstructor
@Data
@Builder
public class ErrorResponse {
    private final int status;
    private String message;
    private final String exceptionMessage;
    private final Instant timestamp;
}