package com.elpdeliveries.util;

import com.elpdeliveries.model.Delivery;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class VroomRouteOptimizationService implements RouteOptimizationService {
    private final HttpClient client = HttpClient.newHttpClient();
    
    @Override
    public List<Integer> optimiseRoute(List<Delivery> deliveries, double depotLat, double depotLong) throws Exception{
        String requestJson = VroomJsonUtil.buildRequest(deliveries, depotLat, depotLong);
        String responseJson = postToVroom(requestJson);
        return VroomJsonUtil.parse(responseJson);
    }

    private String postToVroom(String requestJson) throws Exception {
        String VROOM_URL = "http://" + ConfigManager.getVroomIp() + ":3000";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VROOM_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("VROOM request failed. HTTP " + response.statusCode()
            + "\n" + response.body());
        }

        return response.body();
    }


}
