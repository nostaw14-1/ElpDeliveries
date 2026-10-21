package com.elpdeliveries.util;

import com.elpdeliveries.model.GeocodeResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class NominatimGeocodingService implements GeocodingService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public GeocodeResult geocode(String address) throws IOException {
        String encoded = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String url =
                "https://nominatim.openstreetmap.org/search" +
                        "?q=" + encoded +
                        "&format=jsonv2" +
                        "&limit=1";

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                .header("User-Agent", "ElpDeliveries/1.0").GET().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body());
            if (!root.isArray() || root.isEmpty()) {
                return null;
            }
            JsonNode result = root.get(0);
            Double latitude = result.get("lat").asDouble();
            Double longitude = result.get("lon").asDouble();

            return new GeocodeResult(latitude, longitude);

        } catch (InterruptedException e) {
            ErrorHandler.showError(e.getMessage(), e);
            return null;
        }

    }
}
