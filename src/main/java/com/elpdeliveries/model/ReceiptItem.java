package com.elpdeliveries.model;

import lombok.Getter;
import lombok.Setter;

import java.text.NumberFormat;
import java.util.Locale;

@Setter
@Getter
public class ReceiptItem {
    private String description;
    private int quantity;
    private double price;

    public String getPriceString() {
        return NumberFormat.getCurrencyInstance(Locale.of("en", "AU")).format(price);
    }
}
