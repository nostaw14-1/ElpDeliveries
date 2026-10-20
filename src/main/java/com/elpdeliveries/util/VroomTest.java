package com.elpdeliveries.util;

import com.elpdeliveries.model.Delivery;

import java.sql.SQLException;
import java.util.List;

public class VroomTest {
   public static void main(String[] args) {
       DBConnection db =
               new DBConnection();

       List<Delivery> deliveries =
               null;
       try {
           deliveries = db.getDeliveries(
                   "All Deliveries",
                   null,
                   null,
                   null,
                   null,
                   false);
           System.out.println(deliveries);
       } catch (SQLException e) {
           e.printStackTrace();
       }

       VroomRouteOptimizationService service =
               new VroomRouteOptimizationService();

       List<Integer> order =
               null;
       try {
           order = service.optimiseRoute(
                   deliveries,
                   -33.7675529,
                   151.1875328);
       } catch (Exception e) {
           e.printStackTrace();
       }

       System.out.println(order);
   }
}
