package com.project.hotelBooking.common;

import com.project.hotelBooking.repository.model.*;

import java.time.LocalDate;

import static com.project.hotelBooking.common.CommonTestConstants.*;

public class CommonDatabaseProvider {

    public static final LocalDate BOOKING_START_DATE = LocalDate.now().plusDays(2);
    public static final LocalDate BOOKING_END_DATE = LocalDate.now().plusDays(4);
    public static final String USER_1_USERNAME = "paulsmith";
    public static final String USER_2_USERNAME = "jankowalskie";

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

    public static Room getRoom2(Long hotelId) {
        return Room.builder()
                .roomNumber(16)
                .numberOfPersons(3)
                .standard(4)
                .hotelId(hotelId)
                .build();
    }

    public static final User USER_1 = User.builder()
            .firstName("Paul")
            .lastName("Smith")
            .dateOfBirth(LocalDate.now().minusYears(20))
            .username(USER_1_USERNAME)
            .password("Paulsmith123!")
            .role(ROLE_USER)
            .email(EMAIL_TEST)
            .build();

    public static final User USER_2 = User.builder()
            .firstName("Jan")
            .lastName("Kowalski")
            .dateOfBirth(LocalDate.now().minusYears(25))
            .username(USER_2_USERNAME)
            .password("Jankowalski123!")
            .role(ROLE_USER)
            .email(EMAIL_TEST_2)
            .build();

    public static final User USER_3 = User.builder()
            .firstName("Cris")
            .lastName("Brown")
            .dateOfBirth(LocalDate.now().minusYears(30))
            .username("crisbrown")
            .password("crisbrown123")
            .role(ROLE_USER)
            .email(EMAIL_TEST_3)
            .build();
}
