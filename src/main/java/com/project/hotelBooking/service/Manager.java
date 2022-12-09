package com.project.hotelBooking.service;

import com.project.hotelBooking.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class Manager {

    @Autowired
    private DbService dbService;

    public void initializeDb() {
        Localization localization1 = new Localization(null, "Krakow", "Poland",null);
        Localization localizationSaved1 = dbService.saveLocalization(localization1);

        Hotel hotel1 = new Hotel(null, "hotel1", 2, "Mariot", localizationSaved1.getId(),null);
        Hotel hotel2 = new Hotel(null, "hotel2", 3, "Mariot2", localizationSaved1.getId(),null);
        Hotel hotelSaved1 = dbService.saveHotel(hotel1);
        Hotel hotelSaved2 = dbService.saveHotel(hotel2);

        Room room1 = new Room(null, 2, 3, 2, hotelSaved1.getId(),null);
        Room room2 = new Room(null, 3, 4, 4, hotelSaved1.getId(),null);
        Room roomSaved1 = dbService.saveRoom(room1);
        Room roomSaved2 = dbService.saveRoom(room2);

        User user1 = new User(null, "Jan", "Kowalski", LocalDate.of(1979, 1, 10),null);
        User user2 = new User(null, "Cris", "Brown", LocalDate.of(1984, 2, 15),null);
        User userSaved1 = dbService.saveUser(user1);
        User userSaved2 = dbService.saveUser(user2);

        Booking booking1 = new Booking(null, userSaved1.getId(), roomSaved1.getId(), LocalDate.of(2022, 11, 17), LocalDate.of(2022, 11, 21));
        Booking booking2 = new Booking(null, userSaved1.getId(), roomSaved1.getId(), LocalDate.of(2022, 12, 11), LocalDate.of(2022, 12, 15));

        dbService.saveBooking(booking1);
        dbService.saveBooking(booking2);
    }

    public void clearDb() {
        dbService.deleteAllBookings();
        dbService.deleteAllUsers();
        dbService.deleteAllRooms();
        dbService.deleteAllHotels();
        dbService.deleteAllLocalizations();
    }

}
