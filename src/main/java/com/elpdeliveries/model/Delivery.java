package com.elpdeliveries.model;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.*;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class Delivery {
    private Integer id;
    private LocalDate createdDate;
    private LocalDate dlvDate;
    private String fname;
    private String lname;
    private String phnum;
    private String homePhone;
    private String addr;
    private String notes;
    private String receiptId;
    private String photo;
    private Boolean completed;
    private String completedBy;
    private LocalDate completedDate;
    private Boolean handedToCustomer;
    private Double latitude;
    private Double longitude;
    private Integer routeOrder;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private final BooleanProperty includedInRoute = new SimpleBooleanProperty();

    public Delivery(Integer id, LocalDate createdDate, LocalDate dlvDate,
                    String fname, String lname, String phnum, String homePhone,
                    String addr, String notes,
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
        this.notes = notes;
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
                    String addr, String notes,
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
        this.notes = notes;
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

    @JsonIgnore
    public BooleanProperty includedInRouteProperty() {
        return includedInRoute;
    }

    @JsonGetter("includedInRoute")
    public Boolean getIncludedInRoute() {
        return includedInRoute.get();
    }

    @JsonSetter("includedInRoute")
    public void setIncludedInRoute(Boolean value) {
        includedInRoute.set(Boolean.TRUE.equals(value));
    }

}
