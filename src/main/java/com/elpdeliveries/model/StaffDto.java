package com.elpdeliveries.model;

public record StaffDto(
        String id,
        String firstName,
        String surname
) {
    @Override
    public String toString() {
        return firstName + " " + surname;
    }
}
