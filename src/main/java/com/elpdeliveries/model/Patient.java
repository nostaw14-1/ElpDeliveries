package com.elpdeliveries.model;

public class Patient {
    private String firstName;
    private String surname;
    private String homeAddress;
    private String homeSuburb;
    private String phoneNumber;
    private String mobileNumber;

    public Patient() {
    }

    public Patient(String firstName,
                   String surname,
                   String homeAddress,
                   String homeSuburb,
                   String phoneNumber,
                   String mobileNumber) {

        this.firstName = firstName;
        this.surname = surname;
        this.homeAddress = homeAddress;
        this.homeSuburb = homeSuburb;
        this.phoneNumber = phoneNumber;
        this.mobileNumber = mobileNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getHomeSuburb() {
        return homeSuburb;
    }

    public void setHomeSuburb(String homeSuburb) {
        this.homeSuburb = homeSuburb;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
