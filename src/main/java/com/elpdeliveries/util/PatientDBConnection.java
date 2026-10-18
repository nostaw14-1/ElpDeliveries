package com.elpdeliveries.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.elpdeliveries.model.Patient;
import com.elpdeliveries.model.ReceiptItem;
import com.elpdeliveries.model.ReceiptLookupResult;

public class PatientDBConnection {

    private Connection conn;

    public PatientDBConnection() {

        String url;
        try {
            url = "jdbc:sqlserver://"
                    + ConfigManager.getSqlServer()
                    + ";databaseName="
                    + ConfigManager.getSqlDatabase()
                    + ";encrypt=true;"
                    + "trustServerCertificate=true;";
            conn = DriverManager.getConnection(
                    url,
                    ConfigManager.getSqlUsername(),
                    ConfigManager.getSqlPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ReceiptLookupResult findPatientsById(
            int receiptId)
            throws SQLException {

        ReceiptLookupResult result = new ReceiptLookupResult();


        String sql = """
                SELECT
                    c.FirstName,
                    c.Surname,
                    c.HomeAddress,
                    c.HomeSuburb,
                    c.PhoneNumber,
                    c.MobileNumber,
                    ri.Description,
                    ri.Qty,
                    ri.SellPrice
                FROM Receipts r
                INNER JOIN Clients c
                    ON r.clientID = c.ID
                INNER JOIN ReceiptItems ri
                    ON r.ID = ri.receiptID
                WHERE r.ID = ?
                """;

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setInt(1, receiptId);

        ResultSet rs = ps.executeQuery();

        Patient patient = null;

        while (rs.next()) {

            if (patient == null) {
                patient = new Patient();

                patient.setFirstName(
                        rs.getString("FirstName"));

                patient.setSurname(
                        rs.getString("Surname"));

                patient.setHomeAddress(
                        rs.getString("HomeAddress"));

                patient.setHomeSuburb(
                        rs.getString("HomeSuburb"));

                patient.setPhoneNumber(
                        rs.getString("PhoneNumber"));

                patient.setMobileNumber(
                        rs.getString("MobileNumber"));
            }

            ReceiptItem item = new ReceiptItem();

            item.setDescription(rs.getString("Description"));
            item.setQuantity(rs.getInt("Qty"));
            item.setPrice(rs.getDouble("SellPrice"));

            result.getItems().add(item);

            result.setPatient(patient);

        }

        return result;
    }

    public List<Patient> findPatientsByName(
            String firstName,
            String surname)
            throws SQLException {

        List<Patient> patients = new ArrayList<>();

        String sql = """
                SELECT
                    FirstName,
                    Surname,
                    HomeAddress,
                    HomeSuburb,
                    PhoneNumber,
                    MobileNumber
                FROM Clients
                WHERE
                    FirstName LIKE ?
                    AND Surname LIKE ?
                """;

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, firstName + "%");
        ps.setString(2, surname + "%");

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            Patient patient = new Patient();

            patient.setFirstName(
                    rs.getString("FirstName"));

            patient.setSurname(
                    rs.getString("Surname"));

            patient.setHomeAddress(
                    rs.getString("HomeAddress"));

            patient.setHomeSuburb(
                    rs.getString("HomeSuburb"));

            patient.setPhoneNumber(
                    rs.getString("PhoneNumber"));

            patient.setMobileNumber(
                    rs.getString("MobileNumber"));

            patients.add(patient);
        }

        return patients;
    }

}