package com.project.hotelBooking.controller;

import com.project.hotelBooking.controller.exceptions.BadRequestException;
import com.project.hotelBooking.controller.exceptions.ElementAlreadyExistException;
import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.domain.*;
import com.project.hotelBooking.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
@Component
public class Validator {

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

    //Localization
    public void validateLocalization(Localization localization) {
        validateLocalizationData(localization);
        Localization l = localizationService.getLocalization(localization);
        if (l!=null) throw new ElementAlreadyExistException("Localization already exist");
    }
    public void validateLocalizationEdit(Localization localization) {
        validateLocalizationData(localization);
        validateIfLocalizationExistById(localization.getId());
    }
    private void validateLocalizationData(Localization localization){
        if (localization==null
                || localization.getCity().length()<2
                || localization.getCountry().length()<2) throw new BadRequestException("Bad localization data");
    }
    private void validateIfLocalizationExistById(Long id) {
        localizationService.getLocalizationById(id).orElseThrow(()->new ElementNotFoundException("No such localization"));
    }

    //Hotel
    public void validateHotel(Hotel hotel) {
        validateHotelData(hotel);
        Hotel h = hotelService.getHotel(hotel);
        if (h!=null) throw new ElementAlreadyExistException("Hotel already exist");
        validateIfLocalizationExistById(hotel.getLocalizationId());

    }
    public void validateHotelEdit(Hotel hotel) {
        validateHotelData(hotel);
        validateIfHotelExistById(hotel.getId());
    }
    private void validateHotelData(Hotel hotel) {
        if (hotel==null
                || hotel.getName().length()<2
                || hotel.getNumberOfStars()<1
                || hotel.getNumberOfStars()>5
                || hotel.getHotelChain().length()<2) throw new BadRequestException("Bad hotel data");
    }

    private void validateIfHotelExistById(Long id) {
        hotelService.getHotelById(id).orElseThrow(()->new ElementNotFoundException("No such hotel"));
    }

    //Room
    public void validateRoom(Room room) {
        validateRoomData(room);
        Room r = roomService.getRoom(room);
        if (r!=null) throw new ElementAlreadyExistException("Room already exist");
        validateIfHotelExistById(room.getHotelId());
    }
    public void validateRoomEdit(Room room) {
        validateRoomData(room);
        validateIfRoomExistById(room.getId());
    }
    private void validateRoomData(Room room) {
        if (room==null
                || room.getRoomNumber()<1
                || room.getNumberOfPersons()<1
                || room.getStandard()<1
                || room.getStandard()>5) throw new BadRequestException("Bad room data");
    }
    private void validateIfRoomExistById(Long id) {
        Room r = roomService.getRoomById(id).orElseThrow(()->new ElementNotFoundException("No such room"));
    }

    //User
    public void validateUser(User user) {
        validateUserData(user);
        User u = userService.getUser(user);
        if (u!=null) throw new ElementAlreadyExistException("User already exist");
        u = userService.getUserByUsername(user.getUsername());
        if (u!=null) throw new ElementAlreadyExistException("User already exist");
    }
    public void validateUserEdit(User user) {
        validateUserData(user);
        validateIfUserExistById(user.getId());
    }
    private void validateUserData(User user) {
        if (user==null
                || user.getDateOfBirth()==null
                || user.getFirstName().length()<2
                || user.getLastName().length()<2
                || user.getUsername().length()<2
                || user.getPassword().length()<2
                || user.getRole().length()<2)  throw new BadRequestException("Bad user data");
    }
    private void  validateIfUserExistById(Long id) {
        User u = userService.getUserById(id).orElseThrow(()->new ElementNotFoundException("No such user"));
    }


    //Booking
    public void validateBooking(Booking booking) {
        validateBookingData(booking);
        validateIfUserExistById(booking.getUserId());
        validateIfRoomExistById(booking.getRoomId());
        validateIfRoomIsFree(booking);
    }
    public void validateBookingEdit(Booking booking) {
        validateBookingData(booking);
        validateIfUserExistById(booking.getUserId());
        validateIfRoomExistById(booking.getRoomId());
        validateIfRoomIsFree(booking);
        validateIfBookingExistById(booking.getId());
    }

    public void validateBookingEditUser(Booking booking, Long userId) {
        validateBookingEdit(booking);
        if (userId!=booking.getUserId()) throw new BadRequestException("No privileges to change user id");
    }

    private void validateBookingData(Booking booking) {
        if (booking==null
           || booking.getStart_date().isBefore(LocalDate.now())
           || booking.getStart_date().isAfter(booking.getEnd_date())
           ||booking.getStart_date().equals(booking.getEnd_date())) throw new BadRequestException("Bad booking date");
    }

    private void validateIfRoomIsFree(Booking booking) {
        Long roomId = booking.getRoomId();
        Room room = roomService.getRoomById(roomId)
                .orElseThrow(() -> new ElementNotFoundException("No such room"));
        List<Booking> roomBookings = room.getBookings();
        boolean roomIsFree = false;
        if (roomBookings.size() > 0) {
            for (Booking bookingDatabase : roomBookings) {
                if (bookingDatabase.getEnd_date().isAfter(booking.getStart_date())) {
                    if (bookingDatabase.getStart_date().isBefore(booking.getEnd_date()))
                        throw new ElementAlreadyExistException("Room occupied at this time ");
                }
            }
        }
    }
    private void validateIfBookingExistById(Long id) {
        bookingService.getBookingById(id).orElseThrow(()->new ElementNotFoundException("No such booking"));
    }
}