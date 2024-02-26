package com.project.hotelBooking.service;

import com.project.hotelBooking.repository.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class Manager {

    private final LocalizationService localizationService;
    private final HotelService hotelService;
    private final RoomService roomService;
    private final UserService userService;
    private final BookingService bookingService;
    private final SimpleEmailService simpleEmailService;

    @Value("${email_test}")
    private String EMAIL_TEST;


    public void initializeDb() {
        Localization localization1 = new Localization(null, "Cracow", "Poland",null);
        Localization localizationSaved1 = localizationService.saveLocalization(localization1);

        Hotel hotel1 = new Hotel(null, "Hotel1", 2, "hotelChain1", localizationSaved1.getId(),null);
        Hotel hotel2 = new Hotel(null, "Hotel2", 3, "hotelChain2", localizationSaved1.getId(),null);
        Hotel hotelSaved1 = hotelService.saveHotel(hotel1);
        Hotel hotelSaved2 = hotelService.saveHotel(hotel2);

        Room room1 = new Room(null, 2, 3, 2, hotelSaved1.getId(),null);
        Room room2 = new Room(null, 3, 4, 4, hotelSaved1.getId(),null);
        Room roomSaved1 = roomService.saveRoom(room1);
        Room roomSaved2 = roomService.saveRoom(room2);

        User user1 = new User(null, "user_firstname", "user_lastname", LocalDate.of(2000, 1, 1),"user_role_user","user123","ROLE_USER", EMAIL_TEST, null);
        User user2 = new User(null, "admin_firstname", "admin_lastname", LocalDate.of(2000, 1, 1),"user_role_admin","admin123","ROLE_ADMIN", EMAIL_TEST, null);
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
