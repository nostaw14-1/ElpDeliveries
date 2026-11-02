package com.elpdeliveries.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Patient {
    private String firstName;
    private String surname;
    private String homeAddress;
    private String homeSuburb;
    private String phoneNumber;
    private String mobileNumber;
}
