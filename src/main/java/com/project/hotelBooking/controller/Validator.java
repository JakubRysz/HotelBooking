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
import java.util.regex.Pattern;

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
    protected void validateIfLocalizationExistById(Long id) {
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

    protected void validateIfHotelExistById(Long id) {
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
    protected void validateIfRoomExistById(Long id) {
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
        if (user == null
                || user.getDateOfBirth() == null
                || user.getFirstName().length() < 2
                || user.getLastName().length() < 2
                || user.getUsername().length() < 2
                || user.getPassword().length() < 2
                || user.getRole().length() < 2) throw new BadRequestException("Bad user data");

        validateEmail(user.getEmail());
        validateIfEmailAlreadyExist(user.getEmail());
    }

    protected void  validateIfUserExistById(Long id) {
        User u = userService.getUserById(id).orElseThrow(()->new ElementNotFoundException("No such user"));
    }

    private void validateEmail(String emailAddress) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*"
                +"@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if(!Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches()) throw new BadRequestException("Bad e-mail data");
    }

    private void validateIfEmailAlreadyExist(String email) {
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
                        throw new ElementAlreadyExistException("Room occupied at this time ");
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