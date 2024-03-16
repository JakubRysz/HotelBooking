package com.project.hotelBooking.controller;

import com.project.hotelBooking.repository.model.*;
import com.project.hotelBooking.service.model.BookingServ;
import com.project.hotelBooking.service.model.HotelServ;
import com.project.hotelBooking.service.model.LocalizationServ;

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
    static Stream<Room> roomProvider() {
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(1L, 0, 3, 2, 3L, null)); //bad room number
        rooms.add(new Room(1L, 3, 0, 2, 3L, null)); //bad numberOfPersons
        rooms.add(new Room(1L, 3, 3, 0, 3L, null)); //bad standard
        rooms.add(new Room(1L, 3, 3, 6, 3L, null)); //bad standard
        return rooms.stream();
    }

    //User
    static Stream<User> userProvider() {
        List<User> users = new ArrayList<>();

        users.add(new User(1L, "J", "Kowalski",
                LocalDate.of(1979, 1, 10), "jankowalski", "jankowalski123",
                "ROLE_USER", EMAIL_TEST, null));  //bad firstName

        users.add(new User(1L, "Jan", "K",
                LocalDate.of(1979, 1, 10), "jankowalski", "jankowalski123",
                "ROLE_USER", EMAIL_TEST, null));  //bad LastName

        users.add(new User(1L, "Jan", "Kowalski",
                LocalDate.now().minusYears(100).minusDays(1), "jankowalski", "jankowalski123",
                "ROLE_USER", EMAIL_TEST, null));  //bad dateOfBirth, user has more than 100 years

        users.add(new User(1L, "Jan", "Kowalski",
                LocalDate.now().minusYears(18).plusDays(1), "jankowalski", "jankowalski123",
                "ROLE_USER", EMAIL_TEST, null));  //bad dateOfBirth, user has less than 18 years

        users.add(new User(1L, "Jan", "Kowalski",
                LocalDate.of(1979, 1, 10), "j", "jankowalski123",
                "ROLE_USER", EMAIL_TEST, null));  //bad username

        users.add(new User(1L, "J", "Kowalski",
                LocalDate.of(1979, 1, 10), "jankowalski", "j",
                "ROLE_USER", EMAIL_TEST, null));  //bad password

        return users.stream();
    }

    static Stream<User> userProviderBadEmail() {
        List<User> users = new ArrayList<>();

        users.add(new User(1L, "Jan", "Kowalski",
                LocalDate.of(1979, 1, 10), "jankowalski", "jankowalski123",
                "ROLE_USER", "@gmail.com", null));  //no character before at

        String email = "@gmail.com";
        for (int i = 0; i < 65; i++) email = "a" + email;

        users.add(new User(1L, "Jan", "Kowalski",
                LocalDate.of(1979, 1, 10), "jankowalski", "jankowalski123",
                "ROLE_USER", email, null));  //more than 64 in local part

        users.add(new User(1L, "Jan", "Kowalski",
                LocalDate.of(1979, 1, 10), "jankowalski", "jankowalski123",
                "ROLE_USER", ".jan@gmail.com", null));  //dot at the email begin

        users.add(new User(1L, "Jan", "Kowalski",
                LocalDate.of(1979, 1, 10), "jankowalski", "jankowalski123",
                "ROLE_USER", "jan.@gmail.com", null));  //dot at the email end

        users.add(new User(1L, "Jan", "Kowalski",
                LocalDate.of(1979, 1, 10), "jankowalski", "jankowalski123",
                "ROLE_USER", "jan@gmail.com.", null));  //dot at domain end

        users.add(new User(1L, "Jan", "Kowalski",
                LocalDate.of(1979, 1, 10), "jankowalski", "jankowalski123",
                "ROLE_USER", "jan@gmail..com", null));  //double dot at domain

        users.add(new User(1L, "Jan", "Kowalski",
                LocalDate.of(1979, 1, 10), "jankowalski", "jankowalski123",
                "ROLE_USER", "jan@.com", null));  //bad domain

        return users.stream();
    }

    static Stream<User> userProviderBadRole() {
        List<User> users = new ArrayList<>();

        users.add(new User(1L, "Jan", "Kowalski",
                LocalDate.of(1979, 1, 10), "jankowalski", "jankowalski123",
                "ROLE_USERR", EMAIL_TEST, null));  //bad firstName

        users.add(new User(1L, "Jan", "Kowalski",
                LocalDate.of(1979, 1, 10), "jankowalski", "jankowalski123",
                "ROLE_ADMIM", EMAIL_TEST, null));  //bad firstName

        users.add(new User(1L, "Jan", "Kowalski",
                LocalDate.of(1979, 1, 10), "jankowalski", "jankowalski123",
                "ADMIN", EMAIL_TEST, null));  //bad firstName

        users.add(new User(1L, "Jan", "Kowalski",
                LocalDate.of(1979, 1, 10), "jankowalski", "jankowalski123",
                "USER", EMAIL_TEST, null));  //bad firstName
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
