package com.project.hotelBooking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.common.CommonDatabaseUtils;
import com.project.hotelBooking.controller.mapper.BookingMapper;
import com.project.hotelBooking.controller.mapper.RoomMapper;
import com.project.hotelBooking.controller.model.BookingDto;
import com.project.hotelBooking.repository.*;
import com.project.hotelBooking.repository.model.*;
import com.project.hotelBooking.service.SimpleEmailService;
import com.project.hotelBooking.service.mapper.BookingMapperServ;
import com.project.hotelBooking.service.mapper.RoomMapperServ;
import com.project.hotelBooking.service.model.Mail;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.project.hotelBooking.common.CommonDatabaseProvider.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class
BookingControllerTestSuiteE2ETest {

    public static final String BOOKINGS_URL = "/v1/bookings";
    private final ObjectMapper objectMapper;
    private final LocalizationRepository localizationRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommonDatabaseUtils commonDatabaseUtils;
    private final MockMvc mockMvc;
    private final BookingMapper bookingMapper;
    private final BookingMapperServ bookingMapperServ;

    @MockBean
    private SimpleEmailService emailService;
    private Hotel hotel1;
    private Localization localization1;
    private Room room1;
    private User user1;

    @BeforeEach
    public void initialize() {
        localization1 = localizationRepository.save(LOCALIZATION_1);
        hotel1 = hotelRepository.save(getHotel1(localization1.getId()));
        room1 = roomRepository.save(getRoom1(hotel1.getId()));
        user1 = userRepository.save(USER_1);

        doNothing().when(emailService).send(any(Mail.class));
    }

    @AfterEach
    public void cleanUp() {
        commonDatabaseUtils.clearDatabaseTables();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldCreateBooking() throws Exception {
        //given
        Booking booking1 = Booking.builder()
                .userId(user1.getId())
                .roomId(room1.getId())
                .startDate(BOOKING_START_DATE)
                .endDate(BOOKING_END_DATE)
                .build();
        BookingDto booking1Dto = mapBookingToBookingDto(booking1);

        final String jsonContentNewBooking = objectMapper.writeValueAsString(booking1Dto);
        int bookingsNumberBefore = bookingRepository.findAllBookings(Pageable.unpaged()).size();

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BOOKINGS_URL)
                        .content(jsonContentNewBooking)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        BookingDto booking = getBookingFromResponse(mvcResult);
        int bookingsNumberAfter = bookingRepository.findAllBookings(Pageable.unpaged()).size();
        Booking bookingFromDatabase = bookingRepository.findById(booking.getId()).orElseThrow();
        assertBookingsWithoutId(booking1, bookingFromDatabase);
        assertEquals(bookingsNumberBefore + 1, bookingsNumberAfter);
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldReturnStatus409CreateBooking_whenRoomAlreadyOccupied() throws Exception {
        //given
        Booking booking1 = Booking.builder()
                .userId(user1.getId())
                .roomId(room1.getId())
                .startDate(BOOKING_START_DATE)
                .endDate(BOOKING_END_DATE)
                .build();

        Booking booking2 = Booking.builder()
                .userId(user1.getId())
                .roomId(room1.getId())
                .startDate(BOOKING_START_DATE.plusDays(1))
                .endDate(BOOKING_END_DATE.plusDays(1))
                .build();

        bookingRepository.save(booking1);

        final String jsonContentNewBooking = objectMapper.writeValueAsString(mapBookingToBookingDto(booking2));

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post(BOOKINGS_URL)
                        .content(jsonContentNewBooking)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(409));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldReturnStatus400WhenCreateBooking_withWrongDate() throws Exception {
        //given
        Booking booking1 = Booking.builder()
                .userId(user1.getId())
                .roomId(room1.getId())
                .startDate(BOOKING_START_DATE)
                .endDate(BOOKING_END_DATE)
                .build();

        Booking booking2 = Booking.builder()
                .userId(user1.getId())
                .roomId(room1.getId())
                .startDate(BOOKING_END_DATE)
                .endDate(BOOKING_START_DATE)
                .build();

        bookingRepository.save(booking1);

        final String jsonContentNewBooking = objectMapper.writeValueAsString(mapBookingToBookingDto(booking2));

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post(BOOKINGS_URL)
                        .content(jsonContentNewBooking)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

//    @Test
//    @WithMockUser(roles = {"USER"})
//    public void shouldReturnStatus403CreateBookingUser() throws Exception {
//
//        //given
//        final String jsonContentNewBooking = objectMapper.writeValueAsString(BOOKING_1);
//
//        //when & then
//        mockMvc.perform(MockMvcRequestBuilders.post("/v1/bookings")
//                        .content(jsonContentNewBooking)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().is(403));
//
//        bookingRepository.delete(BOOKING_1);
//        roomRepository.delete(ROOM_1);
//        hotelRepository.delete(HOTEL_1);
//        localizationRepository.delete(LOCALIZATION_1);
//        userRepository.delete(USER_1);
//    }

    // create own booking user

//
//    @Test
//    @WithMockUser(roles = {"ADMIN"})
//    public void shouldGetSingleBooking() throws Exception {
//
//        //given
//        //when
//        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/bookings/" + BOOKING_1.getId()))
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().is(200))
//                .andReturn();
//
//        //then
//        Booking booking = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Booking.class);
//        assertEquals(BOOKING_1.getId(), booking.getId());
//        assertEquals(BOOKING_1.getUserId(), booking.getUserId());
//        assertEquals(BOOKING_1.getRoomId(), booking.getRoomId());
//        assertEquals(BOOKING_1.getStartDate(), booking.getStartDate());
//        assertEquals(BOOKING_1.getEndDate(), booking.getEndDate());
//
//        bookingRepository.delete(BOOKING_1);
//        roomRepository.delete(ROOM_1);
//        hotelRepository.delete(HOTEL_1);
//        localizationRepository.delete(LOCALIZATION_1);
//        userRepository.delete(USER_1);
//    }
//
//    @Test
//    @WithMockUser(roles = {"USER"})
//    public void shouldReturnStatus403GetSingleBookingUser() throws Exception {
//
//        //given
//        //when & then
//        mockMvc.perform(MockMvcRequestBuilders.get("/v1/bookings/" + BOOKING_1.getId()))
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().is(403));
//
//        bookingRepository.delete(BOOKING_1);
//        roomRepository.delete(ROOM_1);
//        hotelRepository.delete(HOTEL_1);
//        localizationRepository.delete(LOCALIZATION_1);
//        userRepository.delete(USER_1);
//    }
//
//    @Test
//    @WithMockUser(roles = {"ADMIN"})
//    public void shouldGetMultipleBookings() throws Exception {
//
//        //given
//
//        Booking newBooking2 = new Booking();
//        newBooking2.setUserId(USER_1.getId());
//        newBooking2.setRoomId(ROOM_1.getId());
//        newBooking2.setStartDate(BOOKING_START_DATE.plusDays(10));
//        newBooking2.setEndDate(BOOKING_END_DATE.plusDays(12));
//        Booking newBooking3 = new Booking();
//        newBooking3.setUserId(USER_1.getId());
//        newBooking3.setRoomId(ROOM_1.getId());
//        newBooking3.setStartDate(BOOKING_START_DATE.plusDays(20));
//        newBooking3.setEndDate(BOOKING_END_DATE.plusDays(22));
//        bookingRepository.save(newBooking2);
//        bookingRepository.save(newBooking3);
//
//        //when
//        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/bookings"))
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().is(200))
//                .andReturn();
//
//        //then
//        Booking[] bookingsArray = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Booking[].class);
//        List<Booking> bookings = new ArrayList<>((Arrays.asList(bookingsArray)));
//
//        assertEquals(3, bookings.size());
//        assertEquals(newBooking3.getId(), bookings.get(2).getId());
//        assertEquals(newBooking3.getUserId(), bookings.get(2).getUserId());
//        assertEquals(newBooking3.getRoomId(), bookings.get(2).getRoomId());
//        assertEquals(newBooking3.getStartDate(), bookings.get(2).getStartDate());
//        assertEquals(newBooking3.getEndDate(), bookings.get(2).getEndDate());
//
//        bookingRepository.delete(BOOKING_1);
//        bookingRepository.delete(newBooking2);
//        bookingRepository.delete(newBooking3);
//        roomRepository.delete(ROOM_1);
//        hotelRepository.delete(HOTEL_1);
//        localizationRepository.delete(LOCALIZATION_1);
//        userRepository.delete(USER_1);
//    }
//
//    @Test
//    @WithMockUser(roles = {"USER"})
//    public void shouldReturnStatus403GetMultipleBookingsUser() throws Exception {
//
//        //given
//        Booking newBooking2 = new Booking();
//        newBooking2.setUserId(USER_1.getId());
//        newBooking2.setRoomId(ROOM_1.getId());
//        newBooking2.setStartDate(BOOKING_START_DATE.plusDays(10));
//        newBooking2.setEndDate(BOOKING_END_DATE.plusDays(12));
//        Booking newBooking3 = new Booking();
//        newBooking3.setUserId(USER_1.getId());
//        newBooking3.setRoomId(ROOM_1.getId());
//        newBooking3.setStartDate(BOOKING_START_DATE.plusDays(20));
//        newBooking3.setEndDate(BOOKING_END_DATE.plusDays(22));
//        bookingRepository.save(newBooking2);
//        bookingRepository.save(newBooking3);
//
//        //when
//        mockMvc.perform(MockMvcRequestBuilders.get("/v1/bookings"))
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().is(403));
//
//        bookingRepository.delete(BOOKING_1);
//        bookingRepository.delete(newBooking2);
//        bookingRepository.delete(newBooking3);
//        roomRepository.delete(ROOM_1);
//        hotelRepository.delete(HOTEL_1);
//        localizationRepository.delete(LOCALIZATION_1);
//        userRepository.delete(USER_1);
//    }
//
//    @Test
//    @WithMockUser(roles = {"ADMIN"})
//    public void shouldEditBooking() throws Exception {
//
//        //given
//        Booking bookingEdited = new Booking();
//        bookingEdited.setId(BOOKING_1.getId());
//        bookingEdited.setUserId(USER_1.getId());
//        bookingEdited.setRoomId(ROOM_1.getId());
//        bookingEdited.setStartDate(BOOKING_START_DATE.plusDays(1));
//        bookingEdited.setEndDate(BOOKING_END_DATE.plusDays(1));
//
//        final String jsonContentBookingEdited = objectMapper.writeValueAsString(bookingEdited);
//
//        //when
//        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/v1/bookings")
//                        .content(jsonContentBookingEdited)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().is(200))
//                .andReturn();
//
//        Booking bookingGet = bookingRepository.findById(bookingEdited.getId()).orElseThrow();
//
//        //then
//        Booking booking = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Booking.class);
//        assertEquals(bookingEdited.getId(), booking.getId());
//        assertEquals(bookingEdited.getUserId(), booking.getUserId());
//        assertEquals(bookingEdited.getRoomId(), booking.getRoomId());
//        assertEquals(bookingEdited.getStartDate(), booking.getStartDate());
//        assertEquals(bookingEdited.getEndDate(), booking.getEndDate());
//
//        bookingRepository.delete(booking);
//        roomRepository.delete(ROOM_1);
//        hotelRepository.delete(HOTEL_1);
//        localizationRepository.delete(LOCALIZATION_1);
//        userRepository.delete(USER_1);
//    }
//
//    @Test
//    @WithMockUser(roles = {"ADMIN"})
//    public void shouldReturnStatus409EditBookingWhenRoomAlreadyOccupied() throws Exception {
//
//        //given
//        Booking newBooking2 = new Booking();
//        newBooking2.setUserId(USER_1.getId());
//        newBooking2.setRoomId(ROOM_1.getId());
//        newBooking2.setStartDate(BOOKING_END_DATE.plusDays(2));
//        newBooking2.setEndDate(BOOKING_END_DATE.plusDays(4));
//        bookingRepository.save(newBooking2);
//
//        Booking bookingEdited = new Booking();
//        bookingEdited.setId(BOOKING_1.getId());
//        bookingEdited.setUserId(USER_1.getId());
//        bookingEdited.setRoomId(ROOM_1.getId());
//        bookingEdited.setStartDate(BOOKING_END_DATE.plusDays(3));
//        bookingEdited.setEndDate(BOOKING_END_DATE.plusDays(5));
//        final String jsonContentBookingEdited = objectMapper.writeValueAsString(bookingEdited);
//
//        //when
//        mockMvc.perform(MockMvcRequestBuilders.put("/v1/bookings")
//                        .content(jsonContentBookingEdited)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().is(409));
//
//        //then
//        bookingRepository.delete(BOOKING_1);
//        bookingRepository.delete(newBooking2);
//        roomRepository.delete(ROOM_1);
//        hotelRepository.delete(HOTEL_1);
//        localizationRepository.delete(LOCALIZATION_1);
//        userRepository.delete(USER_1);
//    }
//
//    @Test
//    @WithMockUser(roles = {"USER"})
//    public void shouldReturnStatus403EditBookingUser() throws Exception {
//
//        //given
//        Booking bookingEdited = new Booking();
//        bookingEdited.setId(BOOKING_1.getId());
//        bookingEdited.setUserId(USER_1.getId());
//        bookingEdited.setRoomId(ROOM_1.getId());
//        bookingEdited.setStartDate(BOOKING_START_DATE.plusDays(1));
//        bookingEdited.setEndDate(BOOKING_END_DATE.plusDays(1));
//        final String jsonContentBookingEdited = objectMapper.writeValueAsString(bookingEdited);
//
//        //when
//        mockMvc.perform(MockMvcRequestBuilders.put("/v1/bookings")
//                        .content(jsonContentBookingEdited)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().is(403));
//
//        bookingRepository.delete(BOOKING_1);
//        roomRepository.delete(ROOM_1);
//        hotelRepository.delete(HOTEL_1);
//        localizationRepository.delete(LOCALIZATION_1);
//        userRepository.delete(USER_1);
//    }
//
//    @Test
//    @WithMockUser(roles = {"ADMIN"})
//    public void shouldDeleteBooking() throws Exception {
//
//        //given
//        int bookingsNumberBefore = bookingRepository.findAllBookings(Pageable.unpaged()).size();
//
//        //when
//        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/bookings/" + BOOKING_1.getId()))
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().is(200));
//
//        int bookingsNumberAfter = bookingRepository.findAllBookings(Pageable.unpaged()).size();
//
//        //then
//        assertEquals(bookingsNumberBefore - 1, bookingsNumberAfter);
//
//        roomRepository.delete(ROOM_1);
//        hotelRepository.delete(HOTEL_1);
//        localizationRepository.delete(LOCALIZATION_1);
//        userRepository.delete(USER_1);
//    }
//
//    @Test
//    @WithMockUser(roles = {"USER"})
//    public void shouldReturnStatus403DeleteBookingUser() throws Exception {
//
//        //given
//        int bookingsNumberBefore = bookingRepository.findAllBookings(Pageable.unpaged()).size();
//
//        //when
//        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/bookings/" + BOOKING_1.getId()))
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().is(403));
//
//        bookingRepository.delete(BOOKING_1);
//        roomRepository.delete(ROOM_1);
//        hotelRepository.delete(HOTEL_1);
//        localizationRepository.delete(LOCALIZATION_1);
//        userRepository.delete(USER_1);
//    }

    private BookingDto mapBookingToBookingDto(Booking booking) {
        return bookingMapper.mapToBookingDto(bookingMapperServ.mapToBooking(booking));
    }

    private Booking mapBookingDtoToBooking(BookingDto bookingDto) {
        return bookingMapperServ.mapToRepositoryBooking(bookingMapper.mapToBooking(bookingDto));
    }

    private BookingDto getBookingFromResponse(MvcResult mvcResult) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), BookingDto.class);
    }

    private static void assertBookingsWithoutId(Booking expectedBooking, Booking actuaLBooking) {
        assertEquals(expectedBooking.getUserId(), actuaLBooking.getUserId());
        assertEquals(expectedBooking.getRoomId(), actuaLBooking.getRoomId());
        assertEquals(expectedBooking.getStartDate(), actuaLBooking.getStartDate());
        assertEquals(expectedBooking.getEndDate(), actuaLBooking.getEndDate());
    }

    private static void assertBookings(Booking expectedBooking, Booking actuaLBooking) {
        assertEquals(expectedBooking.getId(), actuaLBooking.getId());
        assertBookingsWithoutId(expectedBooking, actuaLBooking);
    }
}
