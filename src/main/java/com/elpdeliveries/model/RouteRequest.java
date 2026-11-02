package com.elpdeliveries.model;

import java.util.List;

public class RouteRequest {
    public List<Integer> getDeliveryIds() {
        return deliveryIds;
    }

    private List<Integer> deliveryIds;
    public RouteRequest() {
    }

    public void setDeliveryIds(List<Integer> deliveryIds) {
        this.deliveryIds = deliveryIds;
    }


}
