package com.elpdeliveries.util;

import com.elpdeliveries.model.GeocodeResult;

import java.io.IOException;

public class GeocodeTest {
    void main(String[] args) {
        NominatimGeocodingService service = new NominatimGeocodingService();

        GeocodeResult result = null;
        try {
            result = service.geocode("East Lindfield Pharmacy, East Lindfield");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(result.getLatitude());
        System.out.println(result.getLongitude());
    }
}
