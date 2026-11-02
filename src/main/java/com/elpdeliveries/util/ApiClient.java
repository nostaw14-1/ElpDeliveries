package com.elpdeliveries.util;

import com.elpdeliveries.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class ApiClient {
    private static final String BASE_URL = "http://192.168.0.2:1924/api";
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private static final ApiClient INSTANCE = new ApiClient();

    public static ApiClient getInstance() {return INSTANCE;}

    public List<Delivery> getDeliveries(DeliveryView view, LocalDate date, String fname,
                                        String lname, Integer receiptId, Boolean uncompleted) throws Exception {
        String url = BASE_URL + "/deliveries/search";

        url += "?view=" + view.toString();

        if (date != null) {
            url += "&dlvDate=" + date;
        }

        if (fname != null && !fname.trim().isEmpty()) {
            url += "&firstName=" + URLEncoder.encode(fname, StandardCharsets.UTF_8);
        }

        if (lname != null && !lname.trim().isEmpty()) {
            url += "&lastName=" + URLEncoder.encode(lname, StandardCharsets.UTF_8);
        }

        if (receiptId != null) {
            url += "&receiptId=" + URLEncoder.encode(String.valueOf(receiptId), StandardCharsets.UTF_8);
        }

        if (Boolean.TRUE.equals(uncompleted)) {
            url += "&completed=false";
        }
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        if (response.statusCode() >= 204) {
            throw new RuntimeException(
                    "API Error: " +
                            response.statusCode() +
                            "\n" +
                            response.body());
        }
        List<Delivery> output = mapper.readValue(response.body(), new TypeReference<>() {});
        output.sort(
                Comparator.comparing(
                                Delivery::getRouteOrder,
                                Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(Delivery::getId)
        );
        return output;
    }

    public void addDelivery(Delivery d) throws Exception {
        String json = mapper.writeValueAsString(d);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/deliveries"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() > 204) {
            throw new RuntimeException(
                    "API Error: " +
                            response.statusCode() +
                            "\n" +
                            response.body());
        } else {
            System.out.println(response.statusCode()+"");
        }
    }

    public void updateDelivery(Delivery d) throws Exception {
        String json = mapper.writeValueAsString(d);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/deliveries/" + d.getId()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() > 204) {
            throw new RuntimeException(
                    "API Error: " +
                            response.statusCode() +
                            "\n" +
                            response.body());
        }
    }

    public void deleteDelivery(Integer id) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/deliveries/" + id))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() > 204) {
            throw new RuntimeException(
                    "API Error: " +
                            response.statusCode() +
                            "\n" +
                            response.body());
        }
    }

    public void updateIncludedInRoute(int id, boolean included) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/deliveries/" + id + "/included-in-route?included=" + included))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() > 204) {
            throw new RuntimeException(
                    "API Error: " +
                            response.statusCode() +
                            "\n" +
                            response.body());
        }
    }

    public DeliveryStats getStats() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/deliveries/stats"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() > 204) {
            throw new RuntimeException(
                    "API Error: " +
                            response.statusCode() +
                            "\n" +
                            response.body());
        }
        return mapper.readValue(response.body(), DeliveryStats.class);
    }

    public GeocodeResult geocode(String address) throws Exception {
        String encoded = URLEncoder.encode(address, StandardCharsets.UTF_8);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/geocode?addr=" + encoded))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() > 204) {
            throw new RuntimeException(
                    "API Error: " +
                            response.statusCode() +
                            "\n" +
                            response.body());
        }
        return mapper.readValue(response.body(), GeocodeResult.class);
    }

    public void routeDeliveries(List<Integer> deliveryIds) throws Exception {
        RouteRequest requestBody = new RouteRequest();
        requestBody.setDeliveryIds(deliveryIds);
        String json = mapper.writeValueAsString(requestBody);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/routes/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() > 204) {
            throw new RuntimeException(
                    "API Error: " +
                            response.statusCode() +
                            "\n" +
                            response.body());
        }
    }

}


