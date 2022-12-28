package com.project.hotelBooking.service;

import com.project.hotelBooking.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class Manager {

    @Autowired
    private LocalizationService localizationService;
    @Autowired
    private HotelService hotelService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;

    public void initializeDb() {
        Localization localization1 = new Localization(null, "Krakow", "Poland",null);
        Localization localizationSaved1 = localizationService.saveLocalization(localization1);

        Hotel hotel1 = new Hotel(null, "hotel1", 2, "Mariot", localizationSaved1.getId(),null);
        Hotel hotel2 = new Hotel(null, "hotel2", 3, "Mariot2", localizationSaved1.getId(),null);
        Hotel hotelSaved1 = hotelService.saveHotel(hotel1);
        Hotel hotelSaved2 = hotelService.saveHotel(hotel2);

        Room room1 = new Room(null, 2, 3, 2, hotelSaved1.getId(),null);
        Room room2 = new Room(null, 3, 4, 4, hotelSaved1.getId(),null);
        Room roomSaved1 = roomService.saveRoom(room1);
        Room roomSaved2 = roomService.saveRoom(room2);

        User user1 = new User(null, "Jan", "Kowalski", LocalDate.of(1979, 1, 10),"jankowalski","crisbrown123","ROLE_USER",null);
        User user2 = new User(null, "Cris", "Brown", LocalDate.of(1984, 2, 15),"crisbrown","crisbrown123","ROLE_ADMIN",null);
        User userSaved1 = userService.saveUser(user1);
        User userSaved2 = userService.saveUser(user2);

        Booking booking1 = new Booking(null, userSaved1.getId(), roomSaved1.getId(), LocalDate.of(2023, 02, 17), LocalDate.of(2023, 02, 21));
        Booking booking2 = new Booking(null, userSaved1.getId(), roomSaved1.getId(), LocalDate.of(2023, 02, 11), LocalDate.of(2023, 02, 15));

        bookingService.saveBooking(booking1);
        bookingService.saveBooking(booking2);
    }

    public void clearDb() {
        bookingService.deleteAllBookings();
        userService.deleteAllUsers();
        roomService.deleteAllRooms();
        hotelService.deleteAllHotels();
        localizationService.deleteAllLocalizations();
    }
}
