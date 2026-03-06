package com.jarviswuod.improvedbillingsystem.exception;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class ApiError {

    private Instant timestamp;
    private int status;
    private ErrorCode errorCode;
    private String message;
    private String path;
    private String traceId;
    private Map<String,String> fieldErrors;
}