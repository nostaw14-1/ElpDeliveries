package com.elpdeliveries.util;

import com.elpdeliveries.model.GeocodeResult;

import java.io.IOException;

public interface GeocodingService {
    GeocodeResult geocode(String address) throws IOException, InterruptedException;
}
