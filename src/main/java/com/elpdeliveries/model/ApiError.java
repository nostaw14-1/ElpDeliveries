package com.elpdeliveries.model;

import lombok.Data;

@Data
public class ApiError {
    private String error;
    private String message;
}
