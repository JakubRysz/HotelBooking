package com.project.hotelBooking.common;

import com.project.hotelBooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class CommonDatabaseUtils {

    private final LocalizationRepository localizationRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public void clearDatabaseTables() {
        localizationRepository.deleteAll();
        hotelRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}
