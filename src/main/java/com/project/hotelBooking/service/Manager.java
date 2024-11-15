package com.project.hotelBooking.service;

import com.project.hotelBooking.repository.*;
import com.project.hotelBooking.repository.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class Manager {

    private final LocalizationRepository localizationRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String EMAIL_USER = "test-mail123455677123123231.gmail.com";

    @Value("${email_test}")
    private String EMAIL_TEST;


    public void initializeDb() {
        Localization localization1 = new Localization(null, "Cracow", "Poland",null);
        Localization localizationSaved1 = localizationRepository.save(localization1);

        Hotel hotel1 = new Hotel(null, "Hotel1", 2, "hotelChain1", localizationSaved1.getId(),null);
        Hotel hotel2 = new Hotel(null, "Hotel2", 3, "hotelChain2", localizationSaved1.getId(),null);
        Hotel hotelSaved1 = hotelRepository.save(hotel1);
        Hotel hotelSaved2 = hotelRepository.save(hotel2);

        Room room1 = new Room(null, 2, 3, 2, hotelSaved1.getId(),null);
        Room room2 = new Room(null, 3, 4, 4, hotelSaved1.getId(),null);
        Room roomSaved1 = roomRepository.save(room1);
        Room roomSaved2 = roomRepository.save(room2);

        User user1 = new User(null, "user_firstname", "user_lastname", LocalDate.of(2000, 1, 1),"user_role_user",passwordEncoder.encode("User123!"),"ROLE_USER", EMAIL_USER, null, null, null);
        User user2 = new User(null, "admin_firstname", "admin_lastname", LocalDate.of(2000, 1, 1),"user_role_admin",passwordEncoder.encode("Admin123!"),"ROLE_ADMIN", EMAIL_TEST, null, null, null);
        User userSaved1 = userRepository.save(user1);
        User userSaved2 = userRepository.save(user2);

        Booking booking1 = new Booking(null, userSaved1.getId(), roomSaved1.getId(), LocalDate.of(2023, 02, 17), LocalDate.of(2023, 02, 21));
        Booking booking2 = new Booking(null, userSaved1.getId(), roomSaved1.getId(), LocalDate.of(2023, 02, 11), LocalDate.of(2023, 02, 15));

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
    }

    public void clearDb() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        roomRepository.deleteAll();
        hotelRepository.deleteAll();
        localizationRepository.deleteAll();
    }
}
