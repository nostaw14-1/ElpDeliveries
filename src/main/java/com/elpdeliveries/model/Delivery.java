package com.elpdeliveries.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.time.LocalDate;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Delivery {
    private Integer id;
    private LocalDate createdDate;
    private LocalDate dlvDate;
    private String fname;
    private String lname;
    private String phnum;
    private String homePhone;
    private String addr;
    private String dlvNotes;
    private String receiptId;
    private String photo;
    private Boolean completed;
    private String completedBy;
    private LocalDate completedDate;
    private Boolean handedToCustomer;
    private Double latitude;
    private Double longitude;
    private Integer routeOrder;
    private final BooleanProperty includedInRoute = new SimpleBooleanProperty(true);

    public Delivery() {}

    public Delivery(Integer id, LocalDate createdDate, LocalDate dlvDate,
                    String fname, String lname, String phnum, String homePhone,
                    String addr, String dlvNotes,
                    String receiptId, String photo,
                    Boolean completed, String completedBy,
                    LocalDate completedDate, Boolean handedToCustomer, Double latitude, Double longitude, Integer routeOrder
                    , Boolean includedInRoute) {
        this.id = id;
        this.createdDate = createdDate;
        this.dlvDate = dlvDate;
        this.fname = fname;
        this.lname = lname;
        this.phnum = phnum;
        this.homePhone = homePhone;
        this.addr = addr;
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

    public Delivery(LocalDate createdDate, LocalDate dlvDate,
                    String fname, String lname, String phnum, String homePhone,
                    String addr, String dlvNotes,
                    String receiptId, String photo,
                    Boolean completed, String completedBy,
                    LocalDate completedDate, Boolean handedToCustomer, Double latitude, Double longitude, Integer routeOrder,
                    Boolean includedInRoute) {
        this.createdDate = createdDate;
        this.dlvDate = dlvDate;
        this.fname = fname;
        this.lname = lname;
        this.phnum = phnum;
        this.homePhone = homePhone;
        this.addr = addr;
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

    public Integer getId() {
        return id;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public LocalDate getDlvDate() {
        return dlvDate;
    }

    public void setDlvDate(LocalDate dlvDate) {
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

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Boolean isCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public Boolean isHandedToCustomer() {
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

    public Boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }

    public BooleanProperty includedInRouteProperty() {
        return includedInRoute;
    }

    public Boolean isIncludedInRoute() {
        return includedInRoute.get();
    }

    public void setIncludedInRoute(Boolean value) {
        includedInRoute.set(value);
    }

}
