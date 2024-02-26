package com.project.hotelBooking.controller;

import com.project.hotelBooking.repository.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.project.hotelBooking.controller.ValidatorTest.EMAIL_TEST;

public class Provider {

    //Localization
    static Stream<Localization> localizationProvider() {
        List<Localization> localizations = new ArrayList<>();
        localizations.add(new Localization(1L, "K", "Poland", null)); //bad city
        localizations.add(new Localization(1L, "Cracow", "P", null)); //bad country
        return localizations.stream();
    }

    //Hotel
    protected static Stream<Hotel> hotelProvider() {
        List<Hotel> hotels = new ArrayList<>();
        hotels.add(new Hotel(1L, "Hotel1", 0, "Mariot", 2L, null)); //bad numberOfStars
        hotels.add(new Hotel(1L, "Hotel1", 6, "Mariot", 2L, null)); //bad numberOfStars
        hotels.add(new Hotel(1L, "H", 2, "Mariot", 2L, null)); //bad name
        hotels.add(new Hotel(1L, "Hotel1", 3, "M", 2L, null)); //bad hotelChain
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
    static Stream<Booking> bookingProviderBadDate() {
        List<Booking> bookings = new ArrayList<>();

        bookings.add(new Booking(1L, 3L, 5L,
                LocalDate.of(2024, 01, 15), LocalDate.of(2024, 01, 12))); //booking room when already occupied

        bookings.add(new Booking(1L, 3L, 5L,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(2))); ////booking room when already occupied

        bookings.add(new Booking(1L, 3L, 5L,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(10))); ////booking room when already occupied

        return bookings.stream();
    }

    static Stream<Booking> bookingProviderRoomOccupied() {
        List<Booking> bookings = new ArrayList<>();

        bookings.add(new Booking(2L, 3L, 5L,
                LocalDate.now(), LocalDate.now().plusDays(6))); //booking room when already occupied

        bookings.add(new Booking(2L, 3L, 5L,
                LocalDate.now().plusDays(8), LocalDate.now().plusDays(15))); ////booking room when already occupied

        return bookings.stream();
    }
}
