package com.elpdeliveries.util;
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
                sql += " WHERE DATE(dlvDate) > ?";
                params.add(Date.valueOf(java.time.LocalDate.now()));
                break;
            case "Deliveries for Today":
                sql += " WHERE DATE(dlvDate) = ?";
                params.add(Date.valueOf(java.time.LocalDate.now()));
                break;
            default:
                break;
        }

        if (date != null) {
            sql += sql.contains("WHERE") ? " AND dlvDate = ?" : " WHERE dlvDate = ?";
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
            sql += sql.contains("WHERE") ? " AND receiptNum = ?" : " WHERE receiptNum = ?";
            params.add(receiptId);
        }

        if (Boolean.TRUE.equals(uncompleted)) {
            sql += sql.contains("WHERE") ? " AND completed = ?" : " WHERE completed = ?";
            params.add(false);
        }

        sql += " ORDER BY dlvDate ASC";

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
            routeOrder = (Integer) rs.getObject("routeOrder");

            Delivery delivery = new Delivery(
                    rs.getInt("id"),
                    rs.getDate("createdDate"),
                    rs.getDate("dlvDate"),
                    rs.getString("fname"),
                    rs.getString("lname"),
                    rs.getString("phnum"),
                    rs.getString("homePhone"),
                    rs.getString("addr"),
                    rs.getString("storeNotes"),
                    rs.getString("dlvNotes"),
                    rs.getInt("receiptNum"),
                    rs.getString("photo"),
                    rs.getBoolean("completed"),
                    rs.getString("completedBy"),
                    rs.getDate("completedDate"),
                    rs.getBoolean("handedToCustomer"),
                    latitude,
                    longitude,
                    routeOrder);

            deliveries.add(delivery);
        }

        return deliveries;

    }

    public void addDelivery(Delivery d) throws SQLException {
        String sql = "INSERT INTO deliveries(fname,lname,phnum,receiptNum,addr,dlvDate,storeNotes,dlvNotes,handedToCustomer,completed,completedBy,completedDate,photo,latitude,longitude,routeOrder,homePhone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        setProps(d, ps);
        ps.executeUpdate();
    }

    public void updateDelivery(Delivery d) throws SQLException {
        String sql = "UPDATE deliveries SET fname=?,lname=?,phnum=?,receiptNum=?,addr=?,dlvDate=?,storeNotes=?,dlvNotes=?,handedToCustomer=?,completed=?,completedBy=?,completedDate=?,photo=?,latitude=?,longitude=?,routeOrder=?,homePhone=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        setProps(d, ps);
        ps.executeUpdate();
        ps.setInt(18, d.getId());
    }

    private void setProps(Delivery d, PreparedStatement ps) throws SQLException {
        ps.setString(1, d.getFname());
        ps.setString(2, d.getLname());
        ps.setString(3, d.getPhnum());
        ps.setInt(4, d.getReceiptId());
        ps.setString(5, d.getAddr());
        ps.setDate(6, d.getDlvDate());
        ps.setString(7, d.getStoreNotes());
        ps.setString(8, d.getDlvNotes());
        ps.setBoolean(9, d.isHandedToCustomer());
        ps.setBoolean(10, d.isCompleted());
        ps.setString(11, d.getCompletedBy());
        ps.setDate(12, d.getCompletedDate());
        ps.setString(13, d.getPhoto());
        if (d.getLatitude() == null) {
            ps.setNull(14, Types.DECIMAL);
        } else {
            ps.setDouble(14, d.getLatitude());
        }
        if (d.getLongitude() == null) {
            ps.setNull(15, Types.DECIMAL);
        } else {
            ps.setDouble(15, d.getLongitude());
        }
        if (d.getRouteOrder() == null) {
            ps.setNull(16, Types.INTEGER);
        } else {
            ps.setInt(16, d.getRouteOrder());
        }
        ps.setString(17, d.getHomePhone());


    }

    public void deleteDelivery(int id) throws SQLException {
        String sql = "DELETE FROM deliveries WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public int getTodayCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM deliveries WHERE dlvDate = ?";
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
            e.printStackTrace();
        }
    }

    public void updateRouteOrders(List<Integer> orderedIds) throws SQLException {
        String sql = "UPDATE deliveries SET routeOrder=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < orderedIds.size(); i++) {
            ps.setInt(1, i+1);
            ps.setInt(2, orderedIds.get(i));
            ps.addBatch();
        }
        ps.executeBatch();
    }

}
