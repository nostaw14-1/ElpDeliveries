package com.elpdeliveries.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RouteRequest {
    private List<Integer> deliveryIds;
    private String staffId;
}
