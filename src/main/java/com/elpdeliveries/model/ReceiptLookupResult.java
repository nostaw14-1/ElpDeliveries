package com.elpdeliveries.model;

import java.util.ArrayList;
import java.util.List;

public class ReceiptLookupResult {
    private Patient patient;
    private final List<ReceiptItem> items = new ArrayList<>();

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public List<ReceiptItem> getItems() {
        return items;
    }

}
