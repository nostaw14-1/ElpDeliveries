package com.elpdeliveries.util;

import com.elpdeliveries.model.Delivery;
import com.elpdeliveries.model.GeocodeResult;

public class DeliveryGeocodingService {
    private final DBConnection dao = new DBConnection();
    private final GeocodingService geocoder = new NominatimGeocodingService();

    public void geocodeDelivery(Delivery delivery) throws Exception {
        if (delivery.hasCoordinates()) {
            GeocodeResult result = geocoder.geocode(delivery.getAddr());
            if (result == null) {
                throw new RuntimeException("No results");
            }
            dao.updateCoordinates(delivery.getId(), result.getLatitude(), result.getLongitude());
            delivery.setLatitude(result.getLatitude());
            delivery.setLongitude(result.getLongitude());
        }
    }
}
