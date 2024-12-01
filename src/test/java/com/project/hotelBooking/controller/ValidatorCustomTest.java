package com.project.hotelBooking.controller;

import com.project.hotelBooking.controller.exceptions.BadRequestException;
import com.project.hotelBooking.controller.exceptions.ElementAlreadyExistException;
import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.service.HotelService;
import com.project.hotelBooking.service.LocalizationService;
import com.project.hotelBooking.service.RoomService;
import com.project.hotelBooking.service.UserService;
import com.project.hotelBooking.service.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.project.hotelBooking.common.CommonTestConstants.EMAIL_TEST;
import static com.project.hotelBooking.common.CommonTestConstants.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidatorCustomTest {

    @InjectMocks
    private ValidatorCustom validatorCustomMock;
    @Mock
    private LocalizationService localizationService;
    @Mock
    private HotelService hotelService;
    @Mock
    private RoomService roomService;
    @Mock
    private UserService userService;

    //Localization
    @Test
    public void shouldNotReturnBadRequestExceptionValidateLocalization() {
        //given
        when(localizationService.getLocalizationByCityAndCountry(any(LocalizationServ.class))).thenThrow(ElementNotFoundException.class);
        LocalizationServ localization = LocalizationServ.builder()
                .id(1L)
                .city("Cracow")
                .country("Poland")
                .hotels(null)
                .build();
        //when &then
        assertDoesNotThrow(()-> validatorCustomMock.validateLocalization(localization));
    }
    @ParameterizedTest
    @MethodSource("com.project.hotelBooking.controller.ValidationDataProvider#incorrectLocalizationProvider")
    public void shouldReturnBadRequestExceptionValidateLocalizationWhenBadLocalizationData(LocalizationServ localization) {
        //given
        //when &then
        assertThrows(BadRequestException.class,
                () -> validatorCustomMock.validateLocalization(localization));
    }

    //Hotel
    @Test
    public void shouldNotReturnBadRequestExceptionValidateHotel() {
        //given
        HotelServ hotel = HotelServ.builder()
                .id(1L)
                .name("Hotel1")
                .numberOfStars(2)
                .hotelChain("Marriott")
                .localizationId(2L)
                .rooms(null)
                .build();

        when(hotelService.getHotelByNameAndHotelChain(any(HotelServ.class))).thenThrow(ElementNotFoundException.class);
        when(localizationService.getLocalizationById(any(Long.class))).thenReturn(LocalizationServ.builder().build());
        //when &then
        assertDoesNotThrow(()-> validatorCustomMock.validateHotel(hotel));
    }
    @ParameterizedTest
    @MethodSource("com.project.hotelBooking.controller.ValidationDataProvider#incorrectHotelProvider")
    public void shouldReturnBadRequestExceptionValidateHotelWhenBadHotelData(HotelServ hotel) {
        //given
        //when & then
        assertThrows(BadRequestException.class,
                ()-> validatorCustomMock.validateHotel(hotel),"Bad hotel data");
    }

    //Room
    @Test
    public void shouldNotReturnBadRequestExceptionValidateRoom() {
        //given
        RoomServ room = RoomServ.builder()
                .id(1L)
                .roomNumber(2)
                .numberOfPersons(3)
                .standard(2)
                .hotelId(3L)
                .bookings(null)
                .build();
        when(hotelService.getHotelById(any(Long.class))).thenReturn(HotelServ.builder().build());
        when(roomService.getRoomByRoomNumberAndHotelId(any(RoomServ.class))).thenThrow(ElementNotFoundException.class);
        //when &then
        assertDoesNotThrow(()-> validatorCustomMock.validateRoom(room));
    }
    @ParameterizedTest
    @MethodSource("com.project.hotelBooking.controller.ValidationDataProvider#incorrectRoomProvider")
    public void shouldReturnBadRequestExceptionValidateRoomWhenBadRoomData(RoomServ room) {
        //given
        //when & then
        assertThrows(BadRequestException.class,
                ()-> validatorCustomMock.validateRoom(room),"Bad room data");
    }

    //User
    @Test
    public void shouldNotReturnBadRequestExceptionValidateUser() {
        //given
        UserServ user = UserServ.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .dateOfBirth(LocalDate.now().minusYears(25))
                .username("jankowalski")
                .password("jankowalski123")
                .role(ROLE_USER)
                .email(EMAIL_TEST)
                .bookings(null)
                .build();

        when(userService.getUserByUsername(any(String.class))).thenThrow(ElementNotFoundException.class);
        when(userService.getUserByEmail(any(String.class))).thenThrow(ElementNotFoundException.class);
        //when &then
        assertDoesNotThrow(()-> validatorCustomMock.validateUser(user));
    }

    @ParameterizedTest
    @MethodSource("com.project.hotelBooking.controller.ValidationDataProvider#incorrectUserProvider")
    public void shouldReturnBadRequestExceptionValidateUserWhenBadUserData(UserServ user) {
        //given
        //when & then
        assertThrows(BadRequestException.class,
                ()-> validatorCustomMock.validateUser(user),"Bad user data");
    }
    @ParameterizedTest
    @MethodSource("com.project.hotelBooking.controller.ValidationDataProvider#userProviderBadEmail")
    public void shouldReturnBadRequestExceptionValidateUserWhenBadEmail(UserServ user) {
        //given
        //when & then
        assertThrows(BadRequestException.class,
                ()-> validatorCustomMock.validateUser(user),"Bad email data");
    }

    @ParameterizedTest
    @MethodSource("com.project.hotelBooking.controller.ValidationDataProvider#userProviderBadRole")
    public void shouldReturnBadRequestExceptionValidateUserWhenBadRole(UserServ user) {
        //given
        //when & then
        assertThrows(BadRequestException.class,
                ()-> validatorCustomMock.validateUser(user),"Bad user role");
    }

    //Booking
    @Test
    public void shouldNotReturnBadRequestExceptionValidateBooking() {
        //given
        List<BookingServ> bookings = provideBookingsList();

        BookingServ booking = BookingServ.builder()
                .id(2L)
                .userId(4L)
                .roomId(5L)
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(15))
                .build();

        RoomServ room = RoomServ.builder()
                .id(5L)
                .roomNumber(2)
                .numberOfPersons(3)
                .standard(2)
                .hotelId(3L)
                .bookings(bookings)
                .build();

        when(userService.getUserById(any(Long.class))).thenReturn(UserServ.builder().build());
        when(roomService.getRoomById(5L)).thenReturn(room);
        //when &then
        assertDoesNotThrow(()-> validatorCustomMock.validateBooking(booking));
    }

    private List<BookingServ> provideBookingsList(){
        BookingServ bookingOld = BookingServ.builder()
                .id(1L)
                .userId(3L)
                .roomId(5L)
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(9))
                .build();
        return new ArrayList<>(Arrays.asList(bookingOld));
    }

    @ParameterizedTest
    @MethodSource("com.project.hotelBooking.controller.ValidationDataProvider#bookingProviderBadDate")
    public void shouldReturnBadRequestExceptionValidateBookingWhenBadBookingDate(BookingServ booking) {
        //given
        //when & then
        assertThrows(BadRequestException.class,
                ()-> validatorCustomMock.validateBooking(booking),"Bad booking date");
    }
    @ParameterizedTest
    @MethodSource("com.project.hotelBooking.controller.ValidationDataProvider#bookingProviderRoomOccupied")
    public void shouldReturnElementAlreadyExistExceptionValidateBookingWhenRoomOccupied(BookingServ booking) {
        //given
        List<BookingServ> bookings = provideBookingsList();
        RoomServ room = RoomServ.builder()
                .id(5L)
                .roomNumber(2)
                .numberOfPersons(3)
                .standard(2)
                .hotelId(3L)
                .bookings(bookings)
                .build();
        when(userService.getUserById(any(Long.class))).thenReturn(UserServ.builder().build());
        when(roomService.getRoomById(5L)).thenReturn(room);
        //when & then
        assertThrows(ElementAlreadyExistException.class,
                ()-> validatorCustomMock.validateBooking(booking),"Room occupied at this time");
    }

}