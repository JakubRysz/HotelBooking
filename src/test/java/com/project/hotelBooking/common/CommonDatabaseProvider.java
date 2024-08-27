package com.project.hotelBooking.common;

import com.project.hotelBooking.repository.model.*;

import java.time.LocalDate;

import static com.project.hotelBooking.common.CommonTestConstants.*;

public class CommonDatabaseProvider {

    public static final LocalDate BOOKING_START_DATE = LocalDate.now().plusDays(2);
    public static final LocalDate BOOKING_END_DATE = LocalDate.now().plusDays(4);

    public static final Localization LOCALIZATION_1 = Localization.builder()
            .city(CRACOW_CITY)
            .country(POLAND_COUNTRY)
            .build();

    public static Hotel getHotel1(Long localizationId) {
        return Hotel.builder()
                .name("Hilton1")
                .numberOfStars(3)
                .hotelChain(HILTON_CHAIN)
                .localizationId(localizationId)
                .build();
    }

    public static Room getRoom1(Long hotelId) {
        return Room.builder()
                .roomNumber(15)
                .numberOfPersons(2)
                .standard(4)
                .hotelId(hotelId)
                .build();
    }

    public static final User USER_1 = User.builder()
            .firstName("Paul")
            .lastName("Smith")
            .dateOfBirth(LocalDate.of(1991, 2, 16))
            .username("paulsmith")
            .password("paulsmith123")
            .role(ROLE_USER)
            .email(EMAIL_TEST)
            .build();

    public static final User USER_2 = User.builder()
            .firstName("Jan")
            .lastName("Kowalski")
            .dateOfBirth(LocalDate.of(1992, 3, 15))
            .username("jankowalski")
            .password("jankowalski123")
            .role(ROLE_USER)
            .email(EMAIL_TEST)
            .build();

    public static final User USER_3 = User.builder()
            .firstName("Cris")
            .lastName("Brown")
            .dateOfBirth(LocalDate.of(1993, 4, 15))
            .username("crisbrown")
            .password("crisbrown123")
            .role(ROLE_USER)
            .email(EMAIL_TEST)
            .build();
}
