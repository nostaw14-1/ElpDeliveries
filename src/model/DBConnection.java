package model;
import java.util.*;
import java.io.FileInputStream;
import java.sql.*;
import java.sql.Date;

public class DBConnection {
    protected String server;
    protected String database;
    protected String port;
    protected String url;
    protected String username;
    protected String password;
    protected Connection conn;

    private final Properties props = new Properties();
    

    public DBConnection() {
        try {
            System.out.println(new java.io.File(".").getAbsolutePath());
            props.load(new FileInputStream("config.properties"));
            server = props.getProperty("server", "");
            database = props.getProperty("database", "");
            port = props.getProperty("port", "3306");
            username = props.getProperty("username", "");
            password = props.getProperty("password", "");
            url = "jdbc:mysql://" + server + ":" + port + "/" + database;
            System.out.println(url);
            System.out.println(username);
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Conected");
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    public ArrayList<Delivery> getDeliveries(String view, Date date, String fname, String lname, Integer receiptNum, Boolean uncompleted) throws SQLException {
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

        if (receiptNum != null) {
            sql += sql.contains("WHERE") ? " AND receiptNum = ?" : " WHERE receiptNum = ?";
            params.add(receiptNum);
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

        while (rs.next()) {

            Delivery delivery = new Delivery(
                    rs.getInt("id"),
                    rs.getDate("createdDate"),
                    rs.getDate("dlvDate"),
                    rs.getString("fname"),
                    rs.getString("lname"),
                    rs.getString("phnum"),
                    rs.getString("addr"),
                    rs.getString("storeNotes"),
                    rs.getString("dlvNotes"),
                    rs.getInt("receiptNum"),
                    rs.getString("photo"),
                    rs.getBoolean("completed"),
                    rs.getString("completedBy"),
                    rs.getDate("completedDate"),
                    rs.getBoolean("handedToCustomer"));

            deliveries.add(delivery);
        }

        return deliveries;

    }

    public Delivery getDelivery(int id) throws SQLException {
        String sql = "SELECT * FROM deliveries WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Delivery(
                    rs.getInt("id"),
                    rs.getDate("createdDate"),
                    rs.getDate("dlvDate"),
                    rs.getString("fname"),
                    rs.getString("lname"),
                    rs.getString("phnum"),
                    rs.getString("addr"),
                    rs.getString("storeNotes"),
                    rs.getString("dlvNotes"),
                    rs.getInt("receiptNum"),
                    rs.getString("photo"),
                    rs.getBoolean("completed"),
                    rs.getString("completedBy"),
                    rs.getDate("completedDate"),
                    rs.getBoolean("handedToCustomer"));
        }
        return null;
    }

    public void addDelivery(Delivery d) throws SQLException {
        String sql = "INSERT INTO deliveries(fname,lname,phnum,receiptNum,addr,dlvDate,storeNotes,dlvNotes,handedToCustomer,completed,completedBy,completedDate,photo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, d.getFname());
        ps.setString(2, d.getLname());
        ps.setString(3, d.getPhnum());
        ps.setInt(4, d.getReceiptNum());
        ps.setString(5, d.getAddr());
        ps.setDate(6, d.getDlvDate());
        ps.setString(7, d.getStoreNotes());
        ps.setString(8, d.getDlvNotes());
        ps.setBoolean(9, d.isHandedToCustomer());
        ps.setBoolean(10, d.isCompleted());
        ps.setString(11, d.getCompletedBy());
        ps.setDate(12, d.getCompletedDate());
        ps.setString(13, d.getPhoto());

        ps.executeUpdate();
    }

    public void updateDelivery(Delivery d) throws SQLException {
        String sql = "UPDATE deliveries SET fname=?,lname=?,phnum=?,receiptNum=?,addr=?,dlvDate=?,storeNotes=?,dlvNotes=?,handedToCustomer=?,completed=?,completedBy=?,completedDate=?,photo=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, d.getFname());
        ps.setString(2, d.getLname());
        ps.setString(3, d.getPhnum());
        ps.setInt(4, d.getReceiptNum());
        ps.setString(5, d.getAddr());
        ps.setDate(6, d.getDlvDate());
        ps.setString(7, d.getStoreNotes());
        ps.setString(8, d.getDlvNotes());
        ps.setBoolean(9, d.isHandedToCustomer());
        ps.setBoolean(10, d.isCompleted());
        ps.setString(11, d.getCompletedBy());
        ps.setDate(12, d.getCompletedDate());
        ps.setString(13, d.getPhoto());

        ps.setInt(14, d.getId());

        ps.executeUpdate();
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

}
