package com.elpdeliveries.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReceiptLookupResult {
    private Patient patient;
    private final List<ReceiptItem> items = new ArrayList<>();
}
