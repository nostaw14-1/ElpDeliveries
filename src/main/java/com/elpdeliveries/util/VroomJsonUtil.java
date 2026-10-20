package com.elpdeliveries.util;

import com.elpdeliveries.model.Delivery;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class VroomJsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private VroomJsonUtil() {}

    public static String buildRequest(List<Delivery> deliveries, double depotLatitude, double depotLongitude) throws Exception {
        ObjectNode root = MAPPER.createObjectNode();
        ArrayNode jobs = root.putArray("jobs");

        for (Delivery delivery : deliveries) {
            if (!delivery.hasCoordinates()) {
                throw new IllegalStateException("Delivery " + delivery.getId() + " has no coords");
            }
            ObjectNode job = jobs.addObject();
            job.put("id", delivery.getId());
            ArrayNode location = job.putArray("location");
            location.add(delivery.getLongitude());
            location.add(delivery.getLatitude());
        }

        ArrayNode vehicles = root.putArray("vehicles");
        ObjectNode vehicle = vehicles.addObject();
        vehicle.put("id", 1);
        ArrayNode start = vehicle.putArray("start");
        start.add(depotLongitude);
        start.add(depotLatitude);
        ArrayNode end = vehicle.putArray("end");
        end.add(depotLongitude);
        end.add(depotLatitude);
        return MAPPER.writeValueAsString(root);
    }

    public static List<Integer> parse(String responseJson) throws Exception {
        List<Integer> orderedIds = new ArrayList<>();
        JsonNode root = MAPPER.readTree(responseJson);
        JsonNode routes = root.get("routes");
        if (routes.isEmpty() || !routes.isArray()) {
            return orderedIds;
        }
        JsonNode route = routes.get(0);
        JsonNode steps = route.get("steps");
        if (steps == null || !steps.isArray()) {
            return orderedIds;
        }
        for (JsonNode step : steps) {
            String type = step.path("type").asText();
            if ("job".equals(type)) {
                orderedIds.add(step.get("id").asInt());
            }
        }
        return orderedIds;

    }
}
