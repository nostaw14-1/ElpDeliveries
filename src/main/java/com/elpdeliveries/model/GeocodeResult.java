package com.elpdeliveries.model;

public class GeocodeResult {
    private final Double latitude;
    private final Double longitude;

    public GeocodeResult(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }
}
