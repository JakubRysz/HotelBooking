package com.project.hotelBooking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.common.CommonDatabaseUtils;
import com.project.hotelBooking.controller.mapper.BookingMapper;
import com.project.hotelBooking.controller.model.booking.BookingCreateAdminDto;
import com.project.hotelBooking.controller.model.booking.BookingCreateDto;
import com.project.hotelBooking.controller.model.booking.BookingDto;
import com.project.hotelBooking.controller.model.booking.BookingEditDto;
import com.project.hotelBooking.repository.*;
import com.project.hotelBooking.repository.model.*;
import com.project.hotelBooking.service.SimpleEmailService;
import com.project.hotelBooking.service.mapper.BookingMapperServ;
import com.project.hotelBooking.service.model.BookingServ;
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
import static com.project.hotelBooking.controller.CommonControllerTestConstants.*;
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

    private static final String ROOM_OCCUPIED_MESSAGE = "Conflict: Room occupied at this time";
    private static final String BAD_BOOKING_DATE_MESSAGE = "Bad request: Bad booking date";
    private static final String USER_NOT_OWNER_OF_BOOKING_MESSAGE = "Bad request: User is not owner of booking with id:";

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
    public void shouldCreateBookingAdmin() throws Exception {
        //given
        Booking booking1 = getBooking1();
        BookingCreateAdminDto booking1Dto = mapBookingToBookingCreateAdminDto(booking1);

        final String jsonContentNewBooking = objectMapper.writeValueAsString(booking1Dto);
        int bookingsNumberBefore = bookingRepository.findAllBookings(Pageable.unpaged()).size();

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(ADMIN_BOOKINGS_URL)
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
        assertEquals(bookingsNumberBefore + 1, bookingsNumberAfter);
        assertEqualsBookingsWithoutId(booking1, bookingFromDatabase);
    }

    @Test
    @WithMockUser(username = USER_2_USERNAME)
    public void shouldCreateBookingUser() throws Exception {
        //given
        User user2Saved = userRepository.save(USER_2);
        Booking booking1 = getBooking1();
        //User taken from security context when user is creating booking
        BookingCreateDto booking1Dto = mapBookingToBookingCreateDto(booking1);

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
        assertEquals(bookingsNumberBefore + 1, bookingsNumberAfter);
        assertEqualsBookingsWithoutUser(booking1, bookingFromDatabase);
        assertEquals(user2Saved.getId() , bookingFromDatabase.getUserId());
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

        //when
        MvcResult mvcResult =  mockMvc.perform(MockMvcRequestBuilders.post(ADMIN_BOOKINGS_URL)
                        .content(jsonContentNewBooking)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(409))
                .andReturn();

        // then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(ROOM_OCCUPIED_MESSAGE, responseMessage);
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
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(ADMIN_BOOKINGS_URL)
                        .content(jsonContentNewBooking)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andReturn();

        // then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(BAD_BOOKING_DATE_MESSAGE, responseMessage);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403_createBookingUser_usingAdminEndpoint() throws Exception {
        //given
        Booking booking1 = getBooking1();
        BookingDto booking1Dto = mapBookingToBookingDto(booking1);

        final String jsonContentNewBooking = objectMapper.writeValueAsString(booking1Dto);

        //when & then
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(ADMIN_BOOKINGS_URL)
                        .content(jsonContentNewBooking)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andReturn();

        // then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(ACCESS_DENIED_MESSAGE, responseMessage);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldGetSingleBookingAdmin() throws Exception {
        //given
        Booking booking1 = getBooking1();

        bookingRepository.save(booking1);
        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ADMIN_BOOKINGS_URL + "/" + booking1.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        BookingDto booking = getBookingFromResponse(mvcResult);
        assertEqualsBookings(booking1, mapBookingDtoToBooking(booking));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403_getSingleBookingUser_usingAdminEndpoint() throws Exception {
        //given
        Booking booking1 = getBooking1();

        bookingRepository.save(booking1);
        //when & then
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ADMIN_BOOKINGS_URL + "/" + booking1.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andReturn();

        // then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(ACCESS_DENIED_MESSAGE, responseMessage);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldGetMultipleBookingsAdmin_forGivenUserId() throws Exception {
        //given
        User user2Saved = userRepository.save(USER_2);
        Booking booking1 = getBooking1();
        Booking booking2 = getBooking2(user2Saved);
        Booking booking3 = getBooking3(user2Saved);

        bookingRepository.save(booking1);
        Booking booking2Saved = bookingRepository.save(booking2);
        Booking booking3Saved = bookingRepository.save(booking3);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ADMIN_BOOKINGS_USERS_URL  + "/" + user2Saved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        BookingDto[] bookingsArray = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), BookingDto[].class);
        List<BookingDto> bookings = new ArrayList<>((Arrays.asList(bookingsArray)));

        assertEquals(2, bookings.size());
        assertEqualsBookings(booking2Saved, mapBookingDtoToBooking(bookings.get(0)));
        assertEqualsBookings(booking3Saved, mapBookingDtoToBooking(bookings.get(1)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403_getMultipleBookingsUser_usingAdminEndpoint() throws Exception {
        //given
        //when & then
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ADMIN_BOOKINGS_USERS_URL  + "/" + user1.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andReturn();

        // then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(ACCESS_DENIED_MESSAGE, responseMessage);
    }

    @Test
    @WithMockUser(username = USER_2_USERNAME)
    public void shouldGetMultipleBookingsUser() throws Exception {
        //given
        User user2Saved = userRepository.save(USER_2);
        Booking booking1 = getBooking1();
        Booking booking2 = getBooking2(user2Saved);
        Booking booking3 = getBooking3(user2Saved);

        bookingRepository.save(booking1);
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

        // should only get bookings associated with logged user
        assertEquals(2, bookings.size());
        assertEqualsBookings(booking2Saved, mapBookingDtoToBooking(bookings.get(0)));
        assertEqualsBookings(booking3Saved, mapBookingDtoToBooking(bookings.get(1)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldEditBookingAdmin() throws Exception {
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
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(ADMIN_BOOKINGS_URL)
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
    @WithMockUser(username = USER_2_USERNAME)
    public void shouldEditBookingUser() throws Exception {
        //given
        User user2Saved = userRepository.save(USER_2);
        Booking bookingToBeSaved = getBooking2(user2Saved);
        Booking booking1Saved = bookingRepository.save(bookingToBeSaved);;

        BookingEditDto bookingEdited = BookingEditDto.builder()
                .id(booking1Saved.getId())
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
        assertEqualsBookingsWithoutUser(mapBookingEditDtoToBooking(bookingEdited), bookingFromDatabase);
    }

    @Test
    @WithMockUser(username = USER_2_USERNAME)
    public void shouldReturnStatus400_editBookingUser_whenUserIsNotOwnerOfBooking() throws Exception {
        //given
        userRepository.save(USER_2);
        Booking bookingToBeSaved = getBooking1();
        Booking booking1Saved = bookingRepository.save(bookingToBeSaved);

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
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andReturn();

        //then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        String expectedMessage = USER_NOT_OWNER_OF_BOOKING_MESSAGE + " " + bookingEdited.getId();
        assertEquals(expectedMessage, responseMessage);
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
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(ADMIN_BOOKINGS_URL)
                        .content(jsonContentBookingEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(409))
                .andReturn();

        // then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(ROOM_OCCUPIED_MESSAGE, responseMessage);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403_editBookingUser_usingAdminEndpoint() throws Exception {
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
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(ADMIN_BOOKINGS_URL)
                        .content(jsonContentBookingEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andReturn();

        // then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(ACCESS_DENIED_MESSAGE, responseMessage);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldDeleteBookingAdmin() throws Exception {
        //given
        Booking booking1 = getBooking1();
        Booking booking1Saved = bookingRepository.save(booking1);
        int bookingsNumberBefore = bookingRepository.findAllBookings(Pageable.unpaged()).size();

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete(ADMIN_BOOKINGS_URL + "/" + booking1Saved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        int bookingsNumberAfter = bookingRepository.findAllBookings(Pageable.unpaged()).size();

        //then
        assertEquals(bookingsNumberBefore - 1, bookingsNumberAfter);
    }

    @Test
    @WithMockUser(username = USER_2_USERNAME)
    public void shouldDeleteBookingUser() throws Exception {
        //given
        User user2Saved = userRepository.save(USER_2);
        Booking bookingToBeSaved = getBooking2(user2Saved);
        Booking bookingSaved = bookingRepository.save(bookingToBeSaved);
        int bookingsNumberBefore = bookingRepository.findAllBookings(Pageable.unpaged()).size();

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete(BOOKINGS_URL + "/" + bookingSaved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        int bookingsNumberAfter = bookingRepository.findAllBookings(Pageable.unpaged()).size();

        //then
        assertEquals(bookingsNumberBefore - 1, bookingsNumberAfter);
    }

    @Test
    @WithMockUser(username = USER_2_USERNAME)
    public void shouldReturnStatus403_deleteBookingUser_whenUserIsNotOwnerOfBooking() throws Exception {
        //given
        userRepository.save(USER_2);
        Booking bookingToBeSaved = getBooking1();
        Booking bookingSaved = bookingRepository.save(bookingToBeSaved);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(BOOKINGS_URL + "/" + bookingSaved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andReturn();

        //then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        String expectedMessage = USER_NOT_OWNER_OF_BOOKING_MESSAGE + " " + bookingSaved.getId();
        assertEquals(expectedMessage, responseMessage);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403_deleteBookingUser_usingAdminEndpoint() throws Exception {
        //given
        Booking booking1 = getBooking1();
        Booking booking1Saved = bookingRepository.save(booking1);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(ADMIN_BOOKINGS_URL + "/" + booking1Saved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andReturn();

        // then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(ACCESS_DENIED_MESSAGE, responseMessage);
    }

    private Booking getBooking1() {
        return Booking.builder()
                .userId(user1.getId())
                .roomId(room1.getId())
                .startDate(BOOKING_START_DATE)
                .endDate(BOOKING_END_DATE)
                .build();
    }

    private Booking getBooking2(User user) {
        return Booking.builder()
                .userId(user.getId())
                .roomId(room1.getId())
                .startDate(BOOKING_START_DATE.plusDays(10))
                .endDate(BOOKING_START_DATE.plusDays(12))
                .build();
    }

    private Booking getBooking3(User user) {
        return Booking.builder()
                .userId(user.getId())
                .roomId(room1.getId())
                .startDate(BOOKING_START_DATE.plusDays(20))
                .endDate(BOOKING_START_DATE.plusDays(22))
                .build();
    }

    private BookingDto mapBookingToBookingDto(Booking booking) {
        return bookingMapper.mapToBookingDto(bookingMapperServ.mapToBooking(booking));
    }

    private BookingCreateAdminDto mapBookingToBookingCreateAdminDto(Booking booking) {
        return mapToBookingCreateAdminDto(bookingMapperServ.mapToBooking(booking));
    }

    private BookingCreateDto mapBookingToBookingCreateDto(Booking booking) {
        return mapToBookingCreateDto(bookingMapperServ.mapToBooking(booking));
    }

    private BookingCreateAdminDto mapToBookingCreateAdminDto(BookingServ booking) {
        return BookingCreateAdminDto.builder()
                .userId(booking.getUserId())
                .roomId(booking.getRoomId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .build();
    }

    private BookingCreateDto mapToBookingCreateDto(BookingServ booking) {
        return BookingCreateDto.builder()
                .roomId(booking.getRoomId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .build();
    }

    private Booking mapBookingDtoToBooking(BookingDto bookingDto) {
        return bookingMapperServ.mapToRepositoryBooking(bookingMapper.mapToBooking(bookingDto));
    }

    private Booking mapBookingEditDtoToBooking(BookingEditDto bookingDto) {
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

    private static void assertEqualsBookingsWithoutUser(Booking expectedBooking, Booking actuaLBooking) {
        assertEquals(expectedBooking.getRoomId(), actuaLBooking.getRoomId());
        assertEquals(expectedBooking.getStartDate(), actuaLBooking.getStartDate());
        assertEquals(expectedBooking.getEndDate(), actuaLBooking.getEndDate());
    }

    private static void assertEqualsBookings(Booking expectedBooking, Booking actuaLBooking) {
        assertEquals(expectedBooking.getId(), actuaLBooking.getId());
        assertEqualsBookingsWithoutId(expectedBooking, actuaLBooking);
    }
}
