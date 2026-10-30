package com.elpdeliveries.model;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.sql.Date;

public class Delivery {
    private Integer id;
    private Date createdDate;
    private Date dlvDate;
    private String fname;
    private String lname;
    private String phnum;
    private String homePhone;
    private String addr;
    private String storeNotes;
    private String dlvNotes;
    private int receiptId;
    private String photo;
    private boolean completed;
    private String completedBy;
    private Date completedDate;
    private boolean handedToCustomer;
    private Double latitude;
    private Double longitude;
    private Integer routeOrder;
    private final BooleanProperty includedInRoute = new SimpleBooleanProperty(true);

    public Delivery() {}

    public Delivery(Integer id, Date createdDate, Date dlvDate,
                    String fname, String lname, String phnum, String homePhone,
                    String addr, String storeNotes, String dlvNotes,
                    Integer receiptId, String photo,
                    Boolean completed, String completedBy,
                    Date completedDate, Boolean handedToCustomer, Double latitude, Double longitude, Integer routeOrder
                    , Boolean includedInRoute) {
        this.id = id;
        this.createdDate = createdDate;
        this.dlvDate = dlvDate;
        this.fname = fname;
        this.lname = lname;
        this.phnum = phnum;
        this.homePhone = homePhone;
        this.addr = addr;
        this.storeNotes = storeNotes;
        this.dlvNotes = dlvNotes;
        this.receiptId = receiptId;
        this.photo = photo;
        this.completed = completed;
        this.completedBy = completedBy;
        this.completedDate = completedDate;
        this.handedToCustomer = handedToCustomer;
        this.latitude = latitude;
        this.longitude = longitude;
        this.routeOrder = routeOrder;
        this.includedInRoute.set(includedInRoute);
    }

    public Delivery(Date createdDate, Date dlvDate,
                    String fname, String lname, String phnum, String homePhone,
                    String addr, String storeNotes, String dlvNotes,
                    Integer receiptId, String photo,
                    Boolean completed, String completedBy,
                    Date completedDate, Boolean handedToCustomer, Double latitude, Double longitude, Integer routeOrder,
                    Boolean includedInRoute) {
        this.createdDate = createdDate;
        this.dlvDate = dlvDate;
        this.fname = fname;
        this.lname = lname;
        this.phnum = phnum;
        this.homePhone = homePhone;
        this.addr = addr;
        this.storeNotes = storeNotes;
        this.dlvNotes = dlvNotes;
        this.receiptId = receiptId;
        this.photo = photo;
        this.completed = completed;
        this.completedBy = completedBy;
        this.completedDate = completedDate;
        this.handedToCustomer = handedToCustomer;
        this.latitude = latitude;
        this.longitude = longitude;
        this.routeOrder = routeOrder;
        this.includedInRoute.set(includedInRoute);
    }

    public int getId() {
        return id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getDlvDate() {
        return dlvDate;
    }

    public void setDlvDate(Date dlvDate) {
        this.dlvDate = dlvDate;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPhnum() {
        return phnum;
    }

    public void setPhnum(String phnum) {
        this.phnum = phnum;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }


    public String getNotes() {
        return dlvNotes;
    }

    public void setNotes(String dlvNotes) {
        this.dlvNotes = dlvNotes;
    }

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public boolean isHandedToCustomer() {
        return handedToCustomer;
    }

    public void setHandedToCustomer(boolean handedToCustomer) {
        this.handedToCustomer = handedToCustomer;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getRouteOrder() {
        return routeOrder;
    }

    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }

    public BooleanProperty includedInRouteProperty() {
        return includedInRoute;
    }

    public boolean isIncludedInRoute() {
        return includedInRoute.get();
    }

    public void setIncludedInRoute(boolean value) {
        includedInRoute.set(value);
    }

}
