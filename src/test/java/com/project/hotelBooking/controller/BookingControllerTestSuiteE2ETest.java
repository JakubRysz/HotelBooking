package com.project.hotelBooking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.common.CommonDatabaseUtils;
import com.project.hotelBooking.controller.mapper.BookingMapper;
import com.project.hotelBooking.controller.model.BookingDto;
import com.project.hotelBooking.repository.*;
import com.project.hotelBooking.repository.model.*;
import com.project.hotelBooking.service.SimpleEmailService;
import com.project.hotelBooking.service.mapper.BookingMapperServ;
import com.project.hotelBooking.service.model.Mail;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
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
        Booking booking1 = getBooking1();
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
        assertEqualsBookingsWithoutId(booking1, bookingFromDatabase);
        assertEquals(bookingsNumberBefore + 1, bookingsNumberAfter);
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldReturnStatus409_createBooking_whenRoomAlreadyOccupied() throws Exception {
        //given
        Booking booking1 = getBooking1();

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
    public void shouldReturnStatus400_whenCreateBooking_withWrongDate() throws Exception {
        //given
        Booking booking1 = getBooking1();

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

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403_createBookingUser() throws Exception {
        //given
        Booking booking1 = getBooking1();
        BookingDto booking1Dto = mapBookingToBookingDto(booking1);

        final String jsonContentNewBooking = objectMapper.writeValueAsString(booking1Dto);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post(BOOKINGS_URL)
                        .content(jsonContentNewBooking)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

    // create own booking user


    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldGetSingleBooking() throws Exception {
        //given
        Booking booking1 = getBooking1();

        bookingRepository.save(booking1);
        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(BOOKINGS_URL + "/" + booking1.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        BookingDto booking = getBookingFromResponse(mvcResult);
        assertEqualsBookings(booking1, mapBookingDtoToBooking(booking));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403_getSingleBookingUser() throws Exception {
        //given
        Booking booking1 = getBooking1();

        bookingRepository.save(booking1);
        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get(BOOKINGS_URL + "/" + booking1.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldGetMultipleBookings() throws Exception {
        //given
        Booking booking1 = getBooking1();
        Booking booking2 = Booking.builder()
                .userId(user1.getId())
                .roomId(room1.getId())
                .startDate(BOOKING_START_DATE.plusDays(10))
                .endDate(BOOKING_START_DATE.plusDays(12))
                .build();
        Booking booking3 = Booking.builder()
                .userId(user1.getId())
                .roomId(room1.getId())
                .startDate(BOOKING_START_DATE.plusDays(20))
                .endDate(BOOKING_START_DATE.plusDays(22))
                .build();

        Booking booking1Saved = bookingRepository.save(booking1);
        Booking booking2Saved = bookingRepository.save(booking2);
        Booking booking3Saved = bookingRepository.save(booking3);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(BOOKINGS_URL))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        BookingDto[] bookingsArray = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), BookingDto[].class);
        List<BookingDto> bookings = new ArrayList<>((Arrays.asList(bookingsArray)));

        assertEquals(3, bookings.size());
        assertEqualsBookings(booking1Saved, mapBookingDtoToBooking(bookings.get(0)));
        assertEqualsBookings(booking2Saved, mapBookingDtoToBooking(bookings.get(1)));
        assertEqualsBookings(booking3Saved, mapBookingDtoToBooking(bookings.get(2)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403_getMultipleBookingsUser() throws Exception {
        //given
        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get(BOOKINGS_URL))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldEditBooking() throws Exception {
        //given
        Booking booking1 = getBooking1();
        Booking booking1Saved = bookingRepository.save(booking1);

        BookingDto bookingEdited = BookingDto.builder()
                .id(booking1Saved.getId())
                .userId(booking1Saved.getUserId())
                .roomId(booking1Saved.getRoomId())
                .startDate(booking1Saved.getStartDate().plusDays(10))
                .endDate(booking1Saved.getEndDate().plusDays(12))
                .build();

        final String jsonContentBookingEdited = objectMapper.writeValueAsString(bookingEdited);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(BOOKINGS_URL)
                        .content(jsonContentBookingEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        BookingDto booking = getBookingFromResponse(mvcResult);
        Booking bookingFromDatabase = bookingRepository.findById(booking.getId()).orElseThrow();
        assertEqualsBookings(mapBookingDtoToBooking(bookingEdited), bookingFromDatabase);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldReturnStatus409_editBooking_whenRoomAlreadyOccupied() throws Exception {
        //given
        Booking booking1 = getBooking1();
        Booking booking1Saved = bookingRepository.save(booking1);

        User user2 = userRepository.save(USER_2);

        Booking booking2 = Booking.builder()
                .userId(user2.getId())
                .roomId(room1.getId())
                .startDate(BOOKING_START_DATE.plusDays(10))
                .endDate(BOOKING_END_DATE.plusDays(12))
                .build();

        bookingRepository.save(booking2);

        BookingDto bookingEdited = BookingDto.builder()
                .id(booking1Saved.getId())
                .userId(booking1Saved.getUserId())
                .roomId(booking1Saved.getRoomId())
                .startDate(booking1Saved.getStartDate().plusDays(11))
                .endDate(booking1Saved.getEndDate().plusDays(13))
                .build();

        final String jsonContentBookingEdited = objectMapper.writeValueAsString(bookingEdited);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.put(BOOKINGS_URL)
                        .content(jsonContentBookingEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(409));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403_editBookingUser() throws Exception {
        //given
        Booking booking1 = getBooking1();
        Booking booking1Saved = bookingRepository.save(booking1);

        BookingDto bookingEdited = BookingDto.builder()
                .id(booking1Saved.getId())
                .userId(booking1Saved.getUserId())
                .roomId(booking1Saved.getRoomId())
                .startDate(booking1Saved.getStartDate().plusDays(10))
                .endDate(booking1Saved.getEndDate().plusDays(12))
                .build();

        final String jsonContentBookingEdited = objectMapper.writeValueAsString(bookingEdited);

        //when
        mockMvc.perform(MockMvcRequestBuilders.put(BOOKINGS_URL)
                        .content(jsonContentBookingEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldDeleteBooking() throws Exception {
        //given
        Booking booking1 = getBooking1();
        Booking booking1Saved = bookingRepository.save(booking1);
        int bookingsNumberBefore = bookingRepository.findAllBookings(Pageable.unpaged()).size();

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete(BOOKINGS_URL + "/" + booking1Saved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        int bookingsNumberAfter = bookingRepository.findAllBookings(Pageable.unpaged()).size();

        //then
        assertEquals(bookingsNumberBefore - 1, bookingsNumberAfter);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403_deleteBookingUser() throws Exception {
        //given
        Booking booking1 = getBooking1();
        Booking booking1Saved = bookingRepository.save(booking1);

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete(BOOKINGS_URL + "/" + booking1Saved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

    private Booking getBooking1() {
        return Booking.builder()
                .userId(user1.getId())
                .roomId(room1.getId())
                .startDate(BOOKING_START_DATE)
                .endDate(BOOKING_END_DATE)
                .build();
    }

    private BookingDto mapBookingToBookingDto(Booking booking) {
        return bookingMapper.mapToBookingDto(bookingMapperServ.mapToBooking(booking));
    }

    private Booking mapBookingDtoToBooking(BookingDto bookingDto) {
        return bookingMapperServ.mapToRepositoryBooking(bookingMapper.mapToBooking(bookingDto));
    }

    private BookingDto getBookingFromResponse(MvcResult mvcResult) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), BookingDto.class);
    }

    private static void assertEqualsBookingsWithoutId(Booking expectedBooking, Booking actuaLBooking) {
        assertEquals(expectedBooking.getUserId(), actuaLBooking.getUserId());
        assertEquals(expectedBooking.getRoomId(), actuaLBooking.getRoomId());
        assertEquals(expectedBooking.getStartDate(), actuaLBooking.getStartDate());
        assertEquals(expectedBooking.getEndDate(), actuaLBooking.getEndDate());
    }

    private static void assertEqualsBookings(Booking expectedBooking, Booking actuaLBooking) {
        assertEquals(expectedBooking.getId(), actuaLBooking.getId());
        assertEqualsBookingsWithoutId(expectedBooking, actuaLBooking);
    }
}
