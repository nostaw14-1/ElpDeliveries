package com.elpdeliveries.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RouteDto {
    private Long id;
    private String staffName;
    private LocalDateTime createdAt;
    private String routeStatus;
    private Integer stopCount;
}
