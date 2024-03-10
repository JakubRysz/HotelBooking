package com.project.hotelBooking.controller;

import com.project.hotelBooking.controller.exceptions.BadRequestException;
import com.project.hotelBooking.controller.exceptions.ElementAlreadyExistException;
import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.repository.model.*;
import com.project.hotelBooking.service.*;
import com.project.hotelBooking.service.model.BookingServ;
import com.project.hotelBooking.service.model.HotelServ;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidatorTest {

    @InjectMocks
    private Validator validatorMock;
    @Mock
    private LocalizationService localizationService;
    @Mock
    private HotelService hotelService;
    @Mock
    private RoomService roomService;
    @Mock
    private UserService userService;

    protected static final String EMAIL_TEST = "application.test1010@gmail.com";

    //Localization
    @Test
    public void shouldNotReturnBadRequestExceptionValidateLocalization() {
        //given
        Localization localization = new Localization(1L, "Cracow", "Poland",null);
        //when &then
        assertDoesNotThrow(()->validatorMock.validateLocalization(localization));
    }
    @ParameterizedTest
    @MethodSource("com.project.hotelBooking.controller.Provider#localizationProvider")
    public void shouldReturnBadRequestExceptionValidateLocalizationWhenBadLocalizationData(Localization localization) {
        //given
        //when &then
        assertThrows(BadRequestException.class,
                () -> validatorMock.validateLocalization(localization));
    }

    //Hotel
    @Test
    public void shouldNotReturnBadRequestExceptionValidateHotel() {
        //given
        HotelServ hotel = HotelServ.builder()
                .id(1L)
                .name("Hotel1")
                .numberOfStars(2)
                .hotelChain("Mariot")
                .localizationId(2L)
                .rooms(null)
                .build();

        when(hotelService.getHotelByNameAndHotelChain(any(HotelServ.class))).thenThrow(ElementNotFoundException.class);
        when(localizationService.getLocalizationById(any(Long.class))).thenReturn(Optional.of(new Localization()));
        //when &then
        assertDoesNotThrow(()->validatorMock.validateHotel(hotel));
    }
    @ParameterizedTest
    @MethodSource("com.project.hotelBooking.controller.Provider#hotelProvider")
    public void shouldReturnBadRequestExceptionValidateHotelWhenBadHotelData(HotelServ hotel) {
        //given
        //when & then
        assertThrows(BadRequestException.class,
                ()->validatorMock.validateHotel(hotel),"Bad hotel data");
    }

    //Room
    @Test
    public void shouldNotReturnBadRequestExceptionValidateRoom() {
        //given
        Room room = new Room(1L, 2, 3, 2, 3L,null);
        when(hotelService.getHotelById(any(Long.class))).thenReturn(HotelServ.builder().build());
        //when &then
        assertDoesNotThrow(()->validatorMock.validateRoom(room));
    }
    @ParameterizedTest
    @MethodSource("com.project.hotelBooking.controller.Provider#roomProvider")
    public void shouldReturnBadRequestExceptionValidateRoomWhenBadRoomData(Room room) {
        //given
        //when & then
        assertThrows(BadRequestException.class,
                ()->validatorMock.validateRoom(room),"Bad room data");
    }

    //User
    @Test
    public void shouldNotReturnBadRequestExceptionValidateUser() {
        //given
        User user = new User(1L, "Jan", "Kowalski",
                LocalDate.of(1979, 1, 10),"jankowalski","jankowalski123",
                "ROLE_USER", EMAIL_TEST, null);
        //when &then
        assertDoesNotThrow(()->validatorMock.validateUser(user));
    }
    @ParameterizedTest
    @MethodSource("com.project.hotelBooking.controller.Provider#userProvider")
    public void shouldReturnBadRequestExceptionValidateUserWhenBadUserData(User user) {
        //given
        //when & then
        assertThrows(BadRequestException.class,
                ()->validatorMock.validateUser(user),"Bad user data");
    }
    @ParameterizedTest
    @MethodSource("com.project.hotelBooking.controller.Provider#userProviderBadEmail")
    public void shouldReturnBadRequestExceptionValidateUserWhenBadEmail(User user) {
        //given
        //when & then
        assertThrows(BadRequestException.class,
                ()->validatorMock.validateUser(user),"Bad email data");
    }

    @ParameterizedTest
    @MethodSource("com.project.hotelBooking.controller.Provider#userProviderBadRole")
    public void shouldReturnBadRequestExceptionValidateUserWhenBadRole(User user) {
        //given
        //when & then
        assertThrows(BadRequestException.class,
                ()->validatorMock.validateUser(user),"Bad user role");
    }

    //Booking
    @Test
    public void shouldNotReturnBadRequestExceptionValidateBooking() {
        //given
        List<Booking> bookings = provideBookingsList();

        BookingServ booking = BookingServ.builder()
                .id(2L)
                .userId(4L)
                .roomId(5L)
                .start_date(LocalDate.now().plusDays(10))
                .end_date(LocalDate.now().plusDays(15))
                .build();


        Room room = new Room(5L, 2, 3, 2, 3L,bookings);

        when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(new User()));
        when(roomService.getRoomById(5L)).thenReturn(Optional.of(room));
        //when &then
        assertDoesNotThrow(()->validatorMock.validateBooking(booking));
    }

    //TODO: Change this to BookingServ when implementing RoomServ
    private List<Booking> provideBookingsList(){
        Booking bookingOld = new Booking(1L, 3L, 5L,
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(9));
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingOld);
        return bookings;
    }

    @ParameterizedTest
    @MethodSource("com.project.hotelBooking.controller.Provider#bookingProviderBadDate")
    public void shouldReturnBadRequestExceptionValidateBookingWhenBadBookingDate(BookingServ booking) {
        //given
        //when & then
        assertThrows(BadRequestException.class,
                ()->validatorMock.validateBooking(booking),"Bad booking date");
    }
    @ParameterizedTest
    @MethodSource("com.project.hotelBooking.controller.Provider#bookingProviderRoomOccupied")
    public void shouldReturnElementAlreadyExistExceptionValidateBookingWhenRoomOccupied(BookingServ booking) {
        //given
        List<Booking> bookings = provideBookingsList();
        Room room = new Room(5L, 2, 3, 2, 3L,bookings);
        when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(new User()));
        when(roomService.getRoomById(5L)).thenReturn(Optional.of(room));
        //when & then
        assertThrows(ElementAlreadyExistException.class,
                ()->validatorMock.validateBooking(booking),"Room occupied at this time");
    }

}