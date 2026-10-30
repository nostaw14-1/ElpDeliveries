package com.elpdeliveries.util;

import com.elpdeliveries.model.Delivery;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ApiClient {
    private static final String BASE_URL = "http://192.168.0.2:1924/api";

    private final ObjectMapper mapper = new ObjectMapper();
    public List<Delivery> getDeliveries() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/deliveries")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(response.body(), new TypeReference<>() {});
    }
}
