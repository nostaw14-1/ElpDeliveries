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
import java.time.Duration;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class ApiClient {
    private static final String SERVER = URLEncoder.encode(ConfigManager.getServer(), StandardCharsets.UTF_8);
    private static final String PORT = URLEncoder.encode(ConfigManager.getPort(), StandardCharsets.UTF_8);
    private static final String BASE_URL = "http://"+ SERVER +":"+ PORT +"/api";
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private static final ApiClient INSTANCE = new ApiClient();
    private static final HttpClient CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    public static ApiClient getInstance() {
        return INSTANCE;
    }

    public Exception createException(ApiError error, Integer code) {
        return new RuntimeException("Api Error: " + code + "\n" + error.getMessage());
    }

    public List<Delivery> getDeliveries(DeliveryView view, LocalDate date, String fname,
                                        String lname, String receiptId, Boolean uncompleted) throws Exception {
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
            url += "&receiptId=" + URLEncoder.encode(receiptId, StandardCharsets.UTF_8);
        }

        if (Boolean.TRUE.equals(uncompleted)) {
            url += "&completed=false";
        }
        HttpResponse<String> response;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        AppLogger.info(url + " " + response.statusCode());
        if (response.statusCode() >= 400) {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            throw createException(error, response.statusCode());
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
        String url = BASE_URL + "/deliveries";
        String json = mapper.writeValueAsString(d);
        AppLogger.info(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        AppLogger.info(url + " " + response.statusCode());
        if (response.statusCode() >= 400) {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            throw createException(error, response.statusCode());
        } else {
            System.out.println(response.statusCode()+"");
        }
    }

    public void updateDelivery(Delivery d) throws Exception {
        String url = BASE_URL + "/deliveries/" + d.getId();
        String json = mapper.writeValueAsString(d);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        AppLogger.info(url + " " + response.statusCode());
        if (response.statusCode() >= 400) {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            throw createException(error, response.statusCode());
        }
    }

    public void deleteDelivery(Integer id) throws Exception {
        String url = BASE_URL + "/deliveries/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        AppLogger.info(url + " " + response.statusCode());
        if (response.statusCode() >= 400) {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            throw createException(error, response.statusCode());
        }
    }

    public void updateIncludedInRoute(int id, boolean included) throws Exception {
        String url = BASE_URL + "/deliveries/" + id + "/included-in-route?included=" + included;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        AppLogger.info(url + " " + response.statusCode());
        if (response.statusCode() >= 400) {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            throw createException(error, response.statusCode());
        }
    }

    public DeliveryStats getStats() throws Exception {
        String url = BASE_URL + "/deliveries/stats";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        AppLogger.info(url + " " + response.statusCode());
        if (response.statusCode() >= 400) {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            throw createException(error, response.statusCode());
        }
        return mapper.readValue(response.body(), DeliveryStats.class);
    }

    public GeocodeResult geocode(String address) throws Exception {
        String encoded = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String url = BASE_URL + "/geocode?addr=" + encoded;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        AppLogger.info(url + " " + response.statusCode());
        if (response.statusCode() >= 400) {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            throw createException(error, response.statusCode());
        }
        try {
            return mapper.readValue(response.body(), GeocodeResult.class);
        } catch (Exception e) {
            AppLogger.info(response.body());
            ErrorHandler.showError(e.getMessage(), e);
        }
        return null;
    }

    public RouteResponse routeDeliveries(RouteRequest requestBody) throws Exception {
        String json = mapper.writeValueAsString(requestBody);
        String url = BASE_URL + "/routes/generate";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        AppLogger.info(url + " " + response.statusCode());
        if (response.statusCode() >= 400) {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            throw createException(error, response.statusCode());
        }
        return mapper.readValue(response.body(), RouteResponse.class);
    }

    public PatientLookupDto findPatientsById(String receiptId) throws Exception {
        String url = BASE_URL + "/patients/receipt/" + URLEncoder.encode(receiptId, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        AppLogger.info(url + " " + response.statusCode());
        if (response.statusCode() == 404) {
            throw new RuntimeException("Patient not found for Receipt Number: " + receiptId);
        }
        if (response.statusCode() >= 400) {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            throw createException(error, response.statusCode());
        }
        return mapper.readValue(response.body(), PatientLookupDto.class);
    }

    public List<Patient> findPatientsByName(String firstName, String surname) throws Exception {
        String url = BASE_URL + "/patients/search?firstName=" + URLEncoder.encode(firstName, StandardCharsets.UTF_8) +
                "&surname=" + URLEncoder.encode(surname, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        AppLogger.info(url + " " + response.statusCode());
        if (response.statusCode() == 404) {
            throw new RuntimeException("No Matches Found");
        }
        if (response.statusCode() >= 400) {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            throw createException(error, response.statusCode());
        }
        return mapper.readValue(response.body(), new TypeReference<>() {
        });
    }

    public SettingsDto getSettings() throws Exception {
        String url = BASE_URL + "/settings";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        AppLogger.info(url + " " + response.statusCode());
        if (response.statusCode() >= 400) {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            throw createException(error, response.statusCode());
        }
        return mapper.readValue(response.body(), SettingsDto.class);
    }

    public void updateSettings(SettingsDto settings) throws Exception {
        String json = mapper.writeValueAsString(settings);
        String url = BASE_URL + "/settings";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        AppLogger.info(url + " " + response.statusCode());
        if (response.statusCode() >= 400) {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            throw createException(error, response.statusCode());
        }
    }

    public List<StaffDto> getStaff() throws Exception {
        String url = BASE_URL + "/staff";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        AppLogger.info(url + " " + response.statusCode());
        if (response.statusCode() >= 400) {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            throw createException(error, response.statusCode());
        }
        return mapper.readValue(response.body(), new TypeReference<>() {});
    }

    public List<RouteDto> getRoutes() throws Exception {
        String url = BASE_URL + "/routes";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        AppLogger.info(url + " " + response.statusCode());
        if (response.statusCode() >= 400) {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            throw createException(error, response.statusCode());
        }
        return mapper.readValue(response.body(), new TypeReference<>() {});
    }

    public List<Delivery> getRouteDeliveries(Long routeId, LocalDate date, String fname,
                                        String lname, String receiptId, Boolean uncompleted) throws Exception {
        String url = BASE_URL + "/routes/" + routeId + "/deliveries";

        if (date != null) {
            url += url.contains("?") ? "&" : "?";
            url += "dlvDate=" + date;
        }

        if (fname != null && !fname.trim().isEmpty()) {
            url += url.contains("?") ? "&" : "?";
            url += "firstName=" + URLEncoder.encode(fname, StandardCharsets.UTF_8);
        }

        if (lname != null && !lname.trim().isEmpty()) {
            url += url.contains("?") ? "&" : "?";
            url += "surname=" + URLEncoder.encode(lname, StandardCharsets.UTF_8);
        }

        if (receiptId != null) {
            url += url.contains("?") ? "&" : "?";
            url += "receiptId=" + URLEncoder.encode(receiptId, StandardCharsets.UTF_8);
        }

        if (Boolean.TRUE.equals(uncompleted)) {
            url += url.contains("?") ? "&" : "?";
            url += "completed=false";
        }
        HttpResponse<String> response;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        AppLogger.info(url + " " + response.statusCode());
        if (response.statusCode() >= 400) {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            throw createException(error, response.statusCode());
        }
        return mapper.readValue(response.body(), new TypeReference<>() {});
    }

    public void deleteRoute(Long routeId) throws Exception {
        String url = BASE_URL + "/routes/" + routeId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        AppLogger.info(url + " " + response.statusCode());
        if (response.statusCode() >= 400) {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            throw createException(error, response.statusCode());
        }
    }

}


