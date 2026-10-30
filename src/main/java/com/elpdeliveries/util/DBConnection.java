package com.elpdeliveries.util;
import java.time.LocalDateTime;
import java.util.*;
import java.sql.*;
import java.sql.Date;
import com.elpdeliveries.model.Delivery;

public class DBConnection {
    protected String server;
    protected String database;
    protected String port;
    protected String url;
    protected String username;
    protected String password;
    protected Connection conn;

    public DBConnection() throws Exception {
            server = ConfigManager.getServer();
            port = ConfigManager.getPort();
            database = ConfigManager.getDatabase();
            username = ConfigManager.getUsername();
            password = ConfigManager.getPassword();
            url = "jdbc:mysql://" + server + ":" + port + "/" + database;
            conn = DriverManager.getConnection(url, username, password);
            AppLogger.info("Connecting to " + url);
    }

    public Connection getConnection() {
        return conn;
    }

    public ArrayList<Delivery> getDeliveries(String view, Date date, String fname, String lname, Integer receiptId, Boolean uncompleted) throws SQLException {
        String sql = "SELECT * FROM deliveries";
        ArrayList<Object> params = new ArrayList<>();
        if (view == null) {
            view = "All Deliveries";
        }
        switch (view) {
            case "Future Deliveries":
                sql += " WHERE DATE(dlv_date) > ?";
                params.add(Date.valueOf(java.time.LocalDate.now()));
                break;
            case "Deliveries for Today":
                sql += " WHERE DATE(dlv_date) = ?";
                params.add(Date.valueOf(java.time.LocalDate.now()));
                break;
            default:
                break;
        }

        if (date != null) {
            sql += sql.contains("WHERE") ? " AND dlv_date = ?" : " WHERE dlvDate = ?";
            params.add(date);
        }

        if (fname != null && !fname.trim().isEmpty()) {
            sql += sql.contains("WHERE") ? " AND fname LIKE ?" : " WHERE fname LIKE ?";
            params.add("%" + fname + "%");
        }

        if (lname != null && !lname.trim().isEmpty()) {
            sql += sql.contains("WHERE") ? " AND lname LIKE ?" : " WHERE lname LIKE ?";
            params.add("%" + lname + "%");
        }

        if (receiptId != null) {
            sql += sql.contains("WHERE") ? " AND receipt_id = ?" : " WHERE receipt_ium = ?";
            params.add(receiptId);
        }

        if (Boolean.TRUE.equals(uncompleted)) {
            sql += sql.contains("WHERE") ? " AND completed = ?" : " WHERE completed = ?";
            params.add(false);
        }

        sql += " ORDER BY route_order IS NULL, route_order, id ASC";

        PreparedStatement ps = conn.prepareStatement(sql);

        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        ResultSet rs = ps.executeQuery();

        ArrayList<Delivery> deliveries = new ArrayList<>();

        Double latitude;
        Double longitude;
        Integer routeOrder;

        while (rs.next()) {

            latitude = rs.getBigDecimal("latitude") == null ? null : rs.getBigDecimal("latitude").doubleValue();
            longitude = rs.getBigDecimal("longitude") == null ? null : rs.getBigDecimal("longitude").doubleValue();
            routeOrder = (Integer) rs.getObject("route_order");

            Delivery delivery = new Delivery(
                    rs.getInt("id"),
                    rs.getDate("created_date"),
                    rs.getDate("dlv_date"),
                    rs.getString("fname"),
                    rs.getString("lname"),
                    rs.getString("phnum"),
                    rs.getString("home_phone"),
                    rs.getString("addr"),
                    "",
                    rs.getString("notes"),
                    rs.getInt("receipt_id"),
                    rs.getString("photo"),
                    rs.getBoolean("completed"),
                    rs.getString("completed_by"),
                    rs.getDate("completed_date"),
                    rs.getBoolean("handed_to_customer"),
                    latitude,
                    longitude,
                    routeOrder,
                    rs.getBoolean("included_in_route"));

            deliveries.add(delivery);
        }

        return deliveries;

    }

    public void addDelivery(Delivery d) throws SQLException {
        String sql = "INSERT INTO deliveries(fname,lname,phnum,receipt_id,addr,dlv_date,notes,handed_to_customer,completed,completed_by,completed_date,photo,latitude,longitude,route_order,home_phone,included_in_route) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        setProps(d, ps);
        ps.executeUpdate();
    }

    public void updateDelivery(Delivery d) throws SQLException {
        String sql = "UPDATE deliveries SET fname=?,lname=?,phnum=?,receipt_id=?,addr=?,dlv_date=?,notes=?,handed_to_customer=?,completed=?,completed_by=?,completed_date=?,photo=?,latitude=?,longitude=?,route_order=?,home_phone=?,included_in_route=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        setProps(d, ps);
        ps.setInt(18, d.getId());
        ps.executeUpdate();

    }

    private void setProps(Delivery d, PreparedStatement ps) throws SQLException {
        ps.setString(1, d.getFname());
        ps.setString(2, d.getLname());
        ps.setString(3, d.getPhnum());
        ps.setInt(4, d.getReceiptId());
        ps.setString(5, d.getAddr());
        ps.setDate(6, d.getDlvDate());
        ps.setString(7, d.getNotes());
        ps.setBoolean(8, d.isHandedToCustomer());
        ps.setBoolean(9, d.isCompleted());
        ps.setString(10, d.getCompletedBy());
        ps.setDate(11, d.getCompletedDate());
        ps.setString(12, d.getPhoto());
        if (d.getLatitude() == null) {
            ps.setNull(13, Types.DECIMAL);
        } else {
            ps.setDouble(13, d.getLatitude());
        }
        if (d.getLongitude() == null) {
            ps.setNull(14, Types.DECIMAL);
        } else {
            ps.setDouble(14, d.getLongitude());
        }
        if (d.getRouteOrder() == null) {
            ps.setNull(15, Types.INTEGER);
        } else {
            ps.setInt(15, d.getRouteOrder());
        }
        ps.setString(16, d.getHomePhone());
        ps.setBoolean(17, d.isIncludedInRoute());


    }

    public void deleteDelivery(int id) throws SQLException {
        String sql = "DELETE FROM deliveries WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public int getTodayCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM deliveries WHERE dlv_date = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setDate(1, Date.valueOf(java.time.LocalDate.now()));
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public int getUncompletedCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM deliveries WHERE completed = 0";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public void close() {
        try {
            if (conn != null) {
                conn.close();
            } 
        } catch (SQLException e) {
            AppLogger.warning(e.getMessage());
        }
    }

    public void clearRouteOrders() throws SQLException {
        String sql = "UPDATE deliveries SET route_order=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setNull(1, Types.INTEGER);
        ps.executeUpdate();
    }

    public void updateRouteOrders(List<Integer> orderedIds) throws SQLException {
        clearRouteOrders();
        String sql = "UPDATE deliveries SET route_order=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < orderedIds.size(); i++) {
            ps.setInt(1, i+1);
            ps.setInt(2, orderedIds.get(i));
            ps.addBatch();
        }
        ps.executeBatch();
    }

    public void updateIncludeInRoute(int id, boolean includeInRoute) throws SQLException {
        String sql = "UPDATE deliveries SET include_in_route=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, includeInRoute);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
}
