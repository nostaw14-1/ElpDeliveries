package com.elpdeliveries.util;

import com.elpdeliveries.model.Delivery;

import java.util.List;

public interface RouteOptimizationService {
    List<Integer> optimiseRoute(List<Delivery> deliveries);
}
