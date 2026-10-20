package com.elpdeliveries.util;

import com.elpdeliveries.model.Delivery;
import com.elpdeliveries.model.GeocodeResult;

public class DeliveryGeocodingService {
    private final GeocodingService geocoder = new NominatimGeocodingService();

    public void geocodeDelivery(Delivery delivery) throws Exception {
        GeocodeResult result = geocoder.geocode(delivery.getAddr());
        if (result == null) {
            throw new RuntimeException("Could not find address!");
        }
        delivery.setLatitude(result.getLatitude());
        delivery.setLongitude(result.getLongitude());
    }
}
