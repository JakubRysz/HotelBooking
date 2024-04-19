package com.project.hotelBooking.controller;

import com.project.hotelBooking.service.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.project.hotelBooking.controller.ValidatorTest.EMAIL_TEST;

public class Provider {

    //Localization
    static Stream<LocalizationServ> localizationProvider() {
        LocalizationServ localizationBadCity = LocalizationServ.builder()
                .id(1L)
                .city("K")
                .country("Poland")
                .hotels(null)
                .build();

        LocalizationServ localizationBadCountry = LocalizationServ.builder()
                .id(1L)
                .city("Cracow")
                .country("P")
                .hotels(null)
                .build();

        List<LocalizationServ> localizations = new ArrayList<>(Arrays.asList(
                localizationBadCity,
                localizationBadCountry
        ));

        return localizations.stream();
    }

    //Hotel
    protected static Stream<HotelServ> hotelProvider() {
        HotelServ hotelZeroStars = HotelServ.builder()
                .id(1L)
                .name("Hotel1")
                .numberOfStars(0)
                .hotelChain("Mariot")
                .localizationId(2L)
                .rooms(null)
                .build();

        HotelServ hotelSixStars = HotelServ.builder()
                .id(1L)
                .name("Hotel1")
                .numberOfStars(6)
                .hotelChain("Mariot")
                .localizationId(2L)
                .rooms(null)
                .build();

        HotelServ hotelNameToShort = HotelServ.builder()
                .id(1L)
                .name("H")
                .numberOfStars(3)
                .hotelChain("Mariot")
                .localizationId(2L)
                .rooms(null)
                .build();

        HotelServ hotelHotelChainNameToShort = HotelServ.builder()
                .id(1L)
                .name("Hotel1")
                .numberOfStars(3)
                .hotelChain("M")
                .localizationId(2L)
                .rooms(null)
                .build();

        List<HotelServ> hotels = new ArrayList<>(Arrays.asList(
                hotelZeroStars,
                hotelSixStars,
                hotelNameToShort,
                hotelHotelChainNameToShort
        ));

        return hotels.stream();
    }

    //Room
    static Stream<RoomServ> roomProvider() {

        RoomServ roomBadRoomNumber = RoomServ.builder()
                .id(1L)
                .roomNumber(0)
                .numberOfPersons(3)
                .standard(2)
                .hotelId(3L)
                .bookings(null)
                .build();
        RoomServ roomBadNumberOfPersons = RoomServ.builder()
                .id(1L)
                .roomNumber(3)
                .numberOfPersons(0)
                .standard(2)
                .hotelId(3L)
                .bookings(null)
                .build();
        RoomServ roomBadStandardToLow = RoomServ.builder()
                .id(1L)
                .roomNumber(3)
                .numberOfPersons(3)
                .standard(0)
                .hotelId(3L)
                .bookings(null)
                .build();
        RoomServ roomBadStandardToBig = RoomServ.builder()
                .id(1L)
                .roomNumber(3)
                .numberOfPersons(3)
                .standard(0)
                .hotelId(3L)
                .bookings(null)
                .build();

        List<RoomServ> rooms = new ArrayList<>(Arrays.asList(
                roomBadRoomNumber,
                roomBadNumberOfPersons,
                roomBadStandardToLow,
                roomBadStandardToBig
        ));

        return rooms.stream();
    }

    //User
    static Stream<UserServ> userProvider() {

        UserServ userBadFirstName = UserServ.builder()
                .id(1L)
                .firstName("J")
                .lastName("Kowalski")
                .dateOfBirth(LocalDate.of(1979, 1, 10))
                .username("jankowalski")
                .password("jankowalski123")
                .role("ROLE_USER")
                .email(EMAIL_TEST)
                .bookings(null)
                .build();

        UserServ userBadLastName = UserServ.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("K")
                .dateOfBirth(LocalDate.of(1979, 1, 10))
                .username("jankowalski")
                .password("jankowalski123")
                .role("ROLE_USER")
                .email(EMAIL_TEST)
                .bookings(null)
                .build();

        UserServ userOlderThan100 = UserServ.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .dateOfBirth(LocalDate.now().minusYears(100).minusDays(1))
                .username("jankowalski")
                .password("jankowalski123")
                .role("ROLE_USER")
                .email(EMAIL_TEST)
                .bookings(null)
                .build();

        UserServ userLessThan18 = UserServ.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .dateOfBirth(LocalDate.now().minusYears(18).plusDays(1))
                .username("jankowalski")
                .password("jankowalski123")
                .role("ROLE_USER")
                .email(EMAIL_TEST)
                .bookings(null)
                .build();

        UserServ userBadUsername = UserServ.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .dateOfBirth(LocalDate.of(1979, 1, 10))
                .username("j")
                .password("jankowalski123")
                .role("ROLE_USER")
                .email(EMAIL_TEST)
                .bookings(null)
                .build();

        UserServ userBadPassword = UserServ.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .dateOfBirth(LocalDate.of(1979, 1, 10))
                .username("jankowalski")
                .password("j")
                .role("ROLE_USER")
                .email(EMAIL_TEST)
                .bookings(null)
                .build();

        List<UserServ> users = new ArrayList<>(
                Arrays.asList(
                        userBadFirstName,
                        userBadLastName,
                        userOlderThan100,
                        userLessThan18,
                        userBadUsername,
                        userBadPassword
                )
        );

        return users.stream();
    }

    static Stream<UserServ> userProviderBadEmail() {

        UserServ userBadEmailNoCharacterBeforeAt = UserServ.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .dateOfBirth(LocalDate.of(1979, 1, 10))
                .username("jankowalski")
                .password("jankowalski123")
                .role("ROLE_USER")
                .email("@gmail.com")
                .bookings(null)
                .build();

        StringBuilder email = new StringBuilder("@gmail.com");
        for (int i = 0; i <= 64; i++) email.insert(0, "a");
        String emailToLong = email.toString();

        UserServ userBadEmailMoreThan64CharsInLocalPart = UserServ.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .dateOfBirth(LocalDate.of(1979, 1, 10))
                .username("jankowalski")
                .password("jankowalski123")
                .role("ROLE_USER")
                .email(emailToLong)
                .bookings(null)
                .build();

        UserServ userBadEmailDotAtTheBegin = UserServ.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .dateOfBirth(LocalDate.of(1979, 1, 10))
                .username("jankowalski")
                .password("jankowalski123")
                .role("ROLE_USER")
                .email(".jan@gmail.com")
                .bookings(null)
                .build();

        UserServ userBadEmailDotAtTheEnd = UserServ.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .dateOfBirth(LocalDate.of(1979, 1, 10))
                .username("jankowalski")
                .password("jankowalski123")
                .role("ROLE_USER")
                .email("jan.@gmail.com")
                .bookings(null)
                .build();

        UserServ userBadEmailBadDomain = UserServ.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .dateOfBirth(LocalDate.of(1979, 1, 10))
                .username("jankowalski")
                .password("jankowalski123")
                .role("ROLE_USER")
                .email("jan@.com")
                .bookings(null)
                .build();

        List<UserServ> users = new ArrayList<>(
                Arrays.asList(
                        userBadEmailNoCharacterBeforeAt,
                        userBadEmailMoreThan64CharsInLocalPart,
                        userBadEmailDotAtTheBegin,
                        userBadEmailDotAtTheEnd,
                        userBadEmailBadDomain
                )
        );

        return users.stream();
    }

    static Stream<UserServ> userProviderBadRole() {

        UserServ userBadRoleUser1 = UserServ.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("K")
                .dateOfBirth(LocalDate.of(1979, 1, 10))
                .username("jankowalski")
                .password("jankowalski123")
                .role("ROLE_USERR")
                .email(EMAIL_TEST)
                .bookings(null)
                .build();

        UserServ userBadRoleUser2 = UserServ.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("K")
                .dateOfBirth(LocalDate.of(1979, 1, 10))
                .username("jankowalski")
                .password("jankowalski123")
                .role("USER")
                .email(EMAIL_TEST)
                .bookings(null)
                .build();

        UserServ userBadRoleAdmin1 = UserServ.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("K")
                .dateOfBirth(LocalDate.of(1979, 1, 10))
                .username("jankowalski")
                .password("jankowalski123")
                .role("ROLE_ADMIM")
                .email(EMAIL_TEST)
                .bookings(null)
                .build();

        UserServ userBadRoleAdmin2 = UserServ.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("K")
                .dateOfBirth(LocalDate.of(1979, 1, 10))
                .username("jankowalski")
                .password("jankowalski123")
                .role("ADMIM")
                .email(EMAIL_TEST)
                .bookings(null)
                .build();

        List<UserServ> users = new ArrayList<>(
                Arrays.asList(
                        userBadRoleUser1,
                        userBadRoleUser2,
                        userBadRoleAdmin1,
                        userBadRoleAdmin2
                )
        );

        return users.stream();
    }

    //Booking
    static Stream<BookingServ> bookingProviderBadDate() {

        BookingServ bookingWithStartDateGreaterThenEndDate = BookingServ.builder()
                .id(1L)
                .userId(3L)
                .roomId(5L)
                .start_date(LocalDate.now().plusDays(4))
                .end_date(LocalDate.now().plusDays(2))
                .build();

        BookingServ bookingWithStartDateInPast = BookingServ.builder()
                .id(1L)
                .userId(3L)
                .roomId(5L)
                .start_date(LocalDate.now().minusDays(1))
                .end_date(LocalDate.now().plusDays(2))
                .build();

        BookingServ bookingWithStartDateEqualEndDate = BookingServ.builder()
                .id(1L)
                .userId(3L)
                .roomId(5L)
                .start_date(LocalDate.now().plusDays(2))
                .end_date(LocalDate.now().plusDays(2))
                .build();

        List<BookingServ> bookings = new ArrayList<>(Arrays.asList(
                bookingWithStartDateGreaterThenEndDate,
                bookingWithStartDateInPast,
                bookingWithStartDateEqualEndDate
        ));

        return bookings.stream();
    }

    static Stream<BookingServ> bookingProviderRoomOccupied() {

        BookingServ bookingRoomOccupied1 = BookingServ.builder()
                .id(2L)
                .userId(3L)
                .roomId(5L)
                .start_date(LocalDate.now())
                .end_date(LocalDate.now().plusDays(6))
                .build();

        BookingServ bookingRoomOccupied2 = BookingServ.builder()
                .id(2L)
                .userId(3L)
                .roomId(5L)
                .start_date(LocalDate.now().plusDays(8))
                .end_date(LocalDate.now().plusDays(15))
                .build();

        List<BookingServ> bookings = new ArrayList<>(Arrays.asList(
                bookingRoomOccupied1,
                bookingRoomOccupied2
        ));

        return bookings.stream();
    }
}
