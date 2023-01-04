package com.project.hotelBooking.controller;

import com.project.hotelBooking.controller.exceptions.BadRequestException;
import com.project.hotelBooking.controller.exceptions.ElementAlreadyExistException;
import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.domain.*;
import com.project.hotelBooking.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class Validator {

    private final LocalizationService localizationService;
    private final HotelService hotelService;
    private final RoomService roomService;
    private final UserService userService;
    private final BookingService bookingService;
    String[] roles = new String[]{"ROLE_USER", "ROLE_ADMIN"};
    private final List<String> possibleUserRoles = Arrays.asList(roles);


    //Localization
    public void validateLocalization(Localization localization) {
        validateLocalizationData(localization);
        validateIfLocalizationNotExistByCityAndCountry(localization);
    }
    public void validateLocalizationEdit(Localization localization) {
        validateLocalizationData(localization);
        Localization localizationFromDatabase = validateIfLocalizationExistById(localization.getId());

        if (!localization.getCity().equals(localizationFromDatabase.getCity())
            || !localization.getCountry().equals(localizationFromDatabase.getCountry())) {
            validateIfLocalizationNotExistByCityAndCountry(localization);
        }
    }
    private void validateLocalizationData(Localization localization){
        if (localization==null
                || localization.getCity().length()<2
                || localization.getCountry().length()<2) throw new BadRequestException("Bad localization data");
    }
    protected Localization validateIfLocalizationExistById(Long id) {
        Localization localization =localizationService.getLocalizationById(id).orElseThrow(
                ()->new ElementNotFoundException("No such localization"));
        return localization;
    }

    protected void validateIfLocalizationNotExistByCityAndCountry(Localization localization){
        Localization l = localizationService.getLocalizationByCityAndCountry(localization).orElse(null);
        if (l!=null) throw new ElementAlreadyExistException("Localization already exist");
    }


    //Hotel
    public void validateHotel(Hotel hotel) {
        validateHotelData(hotel);
        validateIfHotelNotExistByNameAndHotelChain(hotel);
        validateIfLocalizationExistById(hotel.getLocalizationId());
    }
    public void validateHotelEdit(Hotel hotel) {
        validateHotelData(hotel);
        Hotel hotelFromDatabase = validateIfHotelExistById(hotel.getId());
        validateIfLocalizationExistById(hotel.getLocalizationId());

        if(!hotel.getName().equals(hotelFromDatabase.getName())
            ||!hotel.getHotelChain().equals(hotelFromDatabase.getHotelChain())) {
            validateIfHotelNotExistByNameAndHotelChain(hotel);
        }
    }
    private void validateHotelData(Hotel hotel) {
        if (hotel==null
                || hotel.getName().length()<2
                || hotel.getNumberOfStars()<1
                || hotel.getNumberOfStars()>5
                || hotel.getHotelChain().length()<2) throw new BadRequestException("Bad hotel data");
    }

    protected Hotel validateIfHotelExistById(Long id) {
        Hotel hotel = hotelService.getHotelById(id).orElseThrow(
                ()->new ElementNotFoundException("No such hotel"));
        return hotel;
    }
    protected void validateIfHotelNotExistByNameAndHotelChain(Hotel hotel) {
        Hotel h = hotelService.getHotelByNameAndHotelChain(hotel).orElse(null);
        if (h != null) throw new ElementAlreadyExistException("Hotel already exist");
    }


    //Room
    public void validateRoom(Room room) {
        validateRoomData(room);
        validateIfRoomNotExistByRoomNumberAndHotelId(room);
        validateIfHotelExistById(room.getHotelId());
    }
    public void validateRoomEdit(Room room) {
        validateRoomData(room);
        Room roomFromDatabase = validateIfRoomExistById(room.getId());
        validateIfHotelExistById(room.getHotelId());

        if(room.getRoomNumber()!=roomFromDatabase.getRoomNumber()
                ||room.getHotelId()!=roomFromDatabase.getHotelId()) {
            validateIfRoomNotExistByRoomNumberAndHotelId(room);
        }
    }
    private void validateRoomData(Room room) {
        if (room==null
                || room.getRoomNumber()<1
                || room.getNumberOfPersons()<1
                || room.getStandard()<1
                || room.getStandard()>5) throw new BadRequestException("Bad room data");
    }
    protected Room validateIfRoomExistById(Long id) {
        Room room = roomService.getRoomById(id).orElseThrow(()->new ElementNotFoundException("No such room"));
        return room;
    }
    protected void validateIfRoomNotExistByRoomNumberAndHotelId(Room room) {
        Room r = roomService.getRoomByRoomNumberAndHotelId(room).orElse(null);
        if (r!=null) throw new ElementAlreadyExistException("Room already exist");
    }


    //User
    public void validateUser(User user) {
        validateUserData(user);
        validateIfUserNotExistByUsername(user.getUsername());
        validateIfEmailNotExist(user.getEmail());
    }
    public void validateUserEdit(User user) {
        validateUserData(user);
        User userFromDatabase = validateIfUserExistById(user.getId());
        if (!user.getEmail().equals(userFromDatabase.getEmail())) {
            validateIfEmailNotExist(user.getEmail());
        }
        if (!user.getUsername().equals(userFromDatabase.getUsername())) {
            validateIfUserNotExistByUsername(user.getUsername());
        }
    }
    private void validateUserData(User user) {
        if (user == null
                || user.getDateOfBirth().isAfter(LocalDate.now().minusYears(18))
                || user.getDateOfBirth().isBefore(LocalDate.now().minusYears(100))
                || user.getFirstName().length() < 2
                || user.getLastName().length() < 2
                || user.getUsername().length() < 2
                || user.getPassword().length() < 2) throw new BadRequestException("Bad user data");

        validateEmail(user.getEmail());
        validateUserRole(user.getRole());
    }

    protected User validateIfUserExistById(Long id) {
        User u = userService.getUserById(id).orElseThrow(()->new ElementNotFoundException("No such user"));
        return u;
    }

    protected void validateIfUserNotExistByUsername (String username) {
        User u = userService.getUserByUsername(username).orElse(null);
        if (u!=null) throw new ElementAlreadyExistException("User with username: "+username+" already exist");
    }

    private void validateEmail(String emailAddress) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*"
                +"@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if(!Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches()) throw new BadRequestException("Bad e-mail data");
    }
    private void validateUserRole(String role) {
        if (!possibleUserRoles.contains(role)) throw new BadRequestException("Bad user role");
    }

    private void validateIfEmailNotExist(String email) {
        User u = userService.getUserByEmail(email).orElse(null);
        if (u!=null) throw new ElementAlreadyExistException("User with e-mail: "+u.getEmail()+" already exist");
    }


    //Booking
    public void validateBooking(Booking booking) {
        validateBookingData(booking);
        validateIfUserExistById(booking.getUserId());
        validateIfRoomExistById(booking.getRoomId());
        validateIfRoomIsFree(booking);
    }
    public void validateBookingEdit(Booking booking, Booking oldBooking) {
        validateIfBookingExistById(booking.getId());
        validateBookingData(booking);
        validateIfUserExistById(booking.getUserId());
        validateIfRoomExistById(booking.getRoomId());
        Booking oldBookingEdit=oldBooking;
        oldBookingEdit.setStart_date(LocalDate.now().minusDays(2));
        oldBookingEdit.setEnd_date(LocalDate.now().minusDays(1));
        bookingService.editBooking(oldBookingEdit);
        try {
            validateIfRoomIsFree(booking);
        } catch (Exception e) {
            bookingService.editBooking(oldBooking);
            throw e;
        }
    }
    public void validateBookingEditUser(Booking booking, Booking oldBooking, Long userId) {
        validateBookingEdit(booking, oldBooking);
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
                        throw new ElementAlreadyExistException("Room occupied at this time");
                }
            }
        }
    }
    protected void validateIfBookingExistById(Long id) {
        bookingService.getBookingById(id).orElseThrow(()->new ElementNotFoundException("No such booking"));
    }
    protected void validateIfUserIsOwnerOfBooking(Booking booking, Long userId) {
        if (booking.getUserId()!=userId)
            throw new BadRequestException("User is not owner of booking with id:"+booking.getId());
    }

}