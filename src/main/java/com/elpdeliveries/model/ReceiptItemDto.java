package com.elpdeliveries.model;

import lombok.Getter;
import lombok.Setter;

import java.text.NumberFormat;
import java.util.Locale;

@Setter
@Getter
public class ReceiptItemDto {
    private String description;
    private int qty;
    private double sellPrice;

    public String getPriceString() {
        return NumberFormat.getCurrencyInstance(Locale.of("en", "AU")).format(sellPrice);
    }
}
