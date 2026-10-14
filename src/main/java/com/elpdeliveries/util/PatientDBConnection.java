package com.elpdeliveries.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.elpdeliveries.model.Patient;

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

    public List<Patient> findPatients(
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