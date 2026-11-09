package com.elpdeliveries.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteResponse {
    private Long routeId;
    private String driverName;
    private Integer deliveryCount;
    private LocalDateTime createdAt;
}
