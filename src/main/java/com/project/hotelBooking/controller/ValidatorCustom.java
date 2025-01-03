package com.project.hotelBooking.controller;

import com.project.hotelBooking.controller.exceptions.BadRequestException;
import com.project.hotelBooking.controller.exceptions.ElementAlreadyExistException;
import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.controller.exceptions.ResourceDeletionConflict;
import com.project.hotelBooking.repository.BookingRepository;
import com.project.hotelBooking.repository.HotelRepository;
import com.project.hotelBooking.repository.LocalizationRepository;
import com.project.hotelBooking.repository.RoomRepository;
import com.project.hotelBooking.service.*;
import com.project.hotelBooking.service.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ValidatorCustom {

    private final LocalizationService localizationService;
    private final LocalizationRepository localizationRepository;
    private final HotelService hotelService;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final RoomService roomService;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private static final String[] ROLES = new String[]{"ROLE_USER", "ROLE_ADMIN"};
    private final List<String> possibleUserRoles = Arrays.asList(ROLES);


    //Localization
    public void validateLocalization(LocalizationServ localization) {
        validateLocalizationData(localization);
        validateIfLocalizationNotExistByCityAndCountry(localization);
    }

    public void validateLocalizationEdit(LocalizationServ localization) {
        validateLocalizationData(localization);
        LocalizationServ localizationFromDatabase = validateIfLocalizationExistById(localization.getId());

        if (!localization.getCity().equals(localizationFromDatabase.getCity())
                || !localization.getCountry().equals(localizationFromDatabase.getCountry())) {
            validateIfLocalizationNotExistByCityAndCountry(localization);
        }
    }

    private void validateLocalizationData(LocalizationServ localization) {
        if (localization == null
                || localization.getCity().length() < 2
                || localization.getCountry().length() < 2) throw new BadRequestException("Bad localization data");
    }

    protected LocalizationServ validateIfLocalizationExistById(Long id) {
        return localizationService.getLocalizationById(id);
    }

    //TODO delegate this exception throw do different layer
    protected void validateIfLocalizationNotExistByCityAndCountry(LocalizationServ localization) {
        try {
            localizationService.getLocalizationByCityAndCountry(localization);
            throw new ElementAlreadyExistException("Localization already exist");
        } catch (ElementNotFoundException ignored) {
        }
    }

    public void validateIfLocalizationHasNoHotels(Long localizationId) {
        List<Long> hotelIds = hotelRepository.findAllIdsByLocalizationIdIn(localizationId);
        if (!hotelIds.isEmpty())
            throw new ResourceDeletionConflict("Localization could not be deleted as there are hotels assigned to this localization");
    }


    //Hotel
    public void validateHotel(HotelServ hotel) {
        validateHotelData(hotel);
        validateIfHotelNotExistByNameAndHotelChain(hotel);
        validateIfLocalizationExistById(hotel.getLocalizationId());
    }

    public void validateHotelEdit(HotelServ hotel) {
        validateHotelData(hotel);
        HotelServ hotelFromDatabase = validateIfHotelExistById(hotel.getId());
        validateIfLocalizationExistById(hotel.getLocalizationId());

        if (!hotel.getName().equals(hotelFromDatabase.getName())
                || !hotel.getHotelChain().equals(hotelFromDatabase.getHotelChain())) {
            validateIfHotelNotExistByNameAndHotelChain(hotel);
        }
    }

    private void validateHotelData(HotelServ hotel) {
        if (hotel == null
                || hotel.getName().length() < 2
                || hotel.getNumberOfStars() < 1
                || hotel.getNumberOfStars() > 5
                || hotel.getHotelChain().length() < 2) throw new BadRequestException("Bad hotel data");
    }

    protected HotelServ validateIfHotelExistById(Long id) {
        return hotelService.getHotelById(id);
    }

    //TODO delegate this exception throw do different layer
    protected void validateIfHotelNotExistByNameAndHotelChain(HotelServ hotel) {
        try {
            hotelService.getHotelByNameAndHotelChain(hotel);
            throw new ElementAlreadyExistException("Hotel already exist");
        } catch (ElementNotFoundException ignored) {
        }
    }

    public void validateIfHotelHasNoBookings(Long hotelId) {
        List<Long> roomIds = roomRepository.findAllIdsByHotelId(hotelId);
        List<Long> bookingIds = bookingRepository.findAllIdsByRoomIdIn(roomIds);
        if (!bookingIds.isEmpty())
            throw new ResourceDeletionConflict("Hotel could not be deleted as there are bookings assigned to this hotel");
    }

    //Room
    public void validateRoom(RoomServ room) {
        validateRoomData(room);
        validateIfRoomNotExistByRoomNumberAndHotelId(room);
        validateIfHotelExistById(room.getHotelId());
    }

    public void validateRoomEdit(RoomServ room) {
        validateRoomData(room);
        RoomServ roomFromDatabase = validateIfRoomExistById(room.getId());
        validateIfHotelExistById(room.getHotelId());

        if (room.getRoomNumber() != roomFromDatabase.getRoomNumber()
                || !room.getHotelId().equals(roomFromDatabase.getHotelId())) {
            validateIfRoomNotExistByRoomNumberAndHotelId(room);
        }
    }

    private void validateRoomData(RoomServ room) {
        if (room == null
                || room.getRoomNumber() < 1
                || room.getNumberOfPersons() < 1
                || room.getStandard() < 1
                || room.getStandard() > 5) throw new BadRequestException("Bad room data");
    }

    protected RoomServ validateIfRoomExistById(Long id) {
        return roomService.getRoomById(id);
    }

    //TODO delegate this exception throw do different layer
    protected void validateIfRoomNotExistByRoomNumberAndHotelId(RoomServ room) {
        try {
            roomService.getRoomByRoomNumberAndHotelId(room);
            throw new ElementAlreadyExistException("Room already exist");
        } catch (ElementNotFoundException ignored) {
        }
    }


    //User
    public void validateUser(UserServ user) {
        validateUserData(user);
        validateIfUserNotExistByUsername(user.getUsername());
        validateIfEmailNotExist(user.getEmail());
    }

    public void validateUserEdit(UserServ user) {
        validateUserDataWithoutPassword(user);
        UserServ userFromDatabase = userService.getUserById(user.getId());
        if (!user.getEmail().equals(userFromDatabase.getEmail())) {
            validateIfEmailNotExist(user.getEmail());
        }
        if (!user.getUsername().equals(userFromDatabase.getUsername())) {
            validateIfUserNotExistByUsername(user.getUsername());
        }
    }

    private void validateUserData(UserServ user) {
        validateUserDataWithoutPassword(user);
        if (user.getPassword().length() < 2) throw new BadRequestException("Bad user data");
    }

    private void validateUserDataWithoutPassword(UserServ user) {
        if (user == null
                || user.getDateOfBirth().isAfter(LocalDate.now().minusYears(18))
                || user.getDateOfBirth().isBefore(LocalDate.now().minusYears(100))
                || user.getFirstName().length() < 2
                || user.getLastName().length() < 2
                || user.getUsername().length() < 2) throw new BadRequestException("Bad user data");

        validateEmail(user.getEmail());
        //validateUserRole(user.getRole());
    }

    protected UserServ validateIfUserExistById(Long id) {
        UserServ user = userService.getUserById(id);
        return user;
    }

    //TODO delegate this exception throw do different layer
    protected void validateIfUserNotExistByUsername(String username) {
        try {
            userService.getUserByUsername(username);
            throw new ElementAlreadyExistException("User already exist");
        } catch (ElementNotFoundException ignored) {
        }
    }

    private void validateEmail(String emailAddress) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*"
                + "@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if (Objects.isNull(emailAddress) ||
                !Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches()
        ) throw new BadRequestException("Bad e-mail data");
    }

    private void validateUserRole(String role) {
        if (!possibleUserRoles.contains(role)) throw new BadRequestException("Bad user role");
    }

    //TODO delegate this exception throw do different layer
    private void validateIfEmailNotExist(String email) {
        try {
            userService.getUserByEmail(email);
            throw new ElementAlreadyExistException("User with e-mail:" + email + "already exist");
        } catch (ElementNotFoundException ignored) {
        }
    }


    //Booking
    public void validateBooking(BookingServ booking) {
        validateBookingData(booking);
        validateIfUserExistById(booking.getUserId());
        validateIfRoomExistById(booking.getRoomId());
        validateIfRoomIsFree(booking);
    }

    public void validateBookingEditAdmin(BookingServ booking) {
        validateBooking(booking);
        validateIfBookingExistById(booking.getId());
    }

    public void validateBookingEditUser(BookingServ booking, BookingServ oldBooking) {
        validateBookingData(booking);
        validateIfBookingExistById(booking.getId());
        validateIfUserIsOwnerOfBooking(oldBooking, booking.getUserId());
    }

    private void validateBookingData(BookingServ booking) {
        if (booking == null
                || booking.getStartDate().isBefore(LocalDate.now())
                || booking.getStartDate().isAfter(booking.getEndDate())
                || booking.getStartDate().equals(booking.getEndDate()))
            throw new BadRequestException("Bad booking date");
    }

    private void validateIfRoomIsFree(BookingServ booking) {
        Long roomId = booking.getRoomId();
        RoomServ room = roomService.getRoomById(roomId);
        List<BookingServ> roomBookings = room.getBookings();
        if (!roomBookings.isEmpty()) {
            for (BookingServ bookingDatabase : roomBookings) {
                if (Objects.equals(bookingDatabase.getId(), booking.getId())) {
                    continue;
                }
                if (bookingDatabase.getEndDate().isAfter(booking.getStartDate())) {
                    if (bookingDatabase.getStartDate().isBefore(booking.getEndDate()))
                        throw new ElementAlreadyExistException("Room occupied at this time");
                }
            }
        }
    }

    protected void validateIfBookingExistById(Long id) {
        bookingService.getBookingById(id);
    }

    protected void validateIfUserIsOwnerOfBooking(BookingServ booking, Long userId) {
        if (!Objects.equals(booking.getUserId(), userId))
            throw new BadRequestException("User is not owner of booking with id: " + booking.getId());
    }
}