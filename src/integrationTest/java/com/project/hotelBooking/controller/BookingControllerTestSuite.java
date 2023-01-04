package com.project.hotelBooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.domain.*;
import com.project.hotelBooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingControllerTestSuite {

    private final ObjectMapper objectMapper;
    private final LocalizationRepository localizationRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final MockMvc mockMvc;
    @Value("${email_test}")
    private String EMAIL_TEST;

    private final Localization newLocalization= new Localization();
    private final Hotel newHotel = new Hotel();
    private final Room newRoom = new Room();
    private final User newUser = new User();
    private final Booking newBooking= new Booking();

    @BeforeEach
    public void initialize() {
        newLocalization.setCity("Krakow");
        newLocalization.setCountry("Poland");
        localizationRepository.save(newLocalization);

        newHotel.setName("Hilton1");
        newHotel.setNumberOfStars(3);
        newHotel.setHotelChain("Hilton");
        newHotel.setLocalizationId(newLocalization.getId());
        hotelRepository.save(newHotel);

        newRoom.setRoomNumber(15);
        newRoom.setNumberOfPersons(2);
        newRoom.setStandard(4);
        newRoom.setHotelId(newHotel.getId());
        roomRepository.save(newRoom);

        newUser.setFirstName("Poul");
        newUser.setLastName("Smith");
        newUser.setDateOfBirth(LocalDate.of(1991,2,16));
        newUser.setUsername("paulsmith");
        newUser.setPassword("paulsmith123");
        newUser.setRole("ROLE_USER");
        newUser.setEmail(EMAIL_TEST);
        userRepository.save(newUser);

        newBooking.setUserId(newUser.getId());
        newBooking.setRoomId(newRoom.getId());
        newBooking.setStart_date(LocalDate.of(2023,07,10));
        newBooking.setEnd_date(LocalDate.of(2023,07,12));
        bookingRepository.save(newBooking);
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldCreateBooking() throws Exception {

        //given
        bookingRepository.delete(newBooking);

        final String jsonContentNewBooking = objectMapper.writeValueAsString(newBooking);
        int bookingsNumberBefore = bookingRepository.findAllBookings(Pageable.unpaged()).size();

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/v1/bookings")
                        .content(jsonContentNewBooking)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        Booking booking = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Booking.class);
        int bookingsNumberAfter = bookingRepository.findAllBookings(Pageable.unpaged()).size();
        assertEquals(newBooking.getUserId(), booking.getUserId());
        assertEquals(newBooking.getRoomId(), booking.getRoomId());
        assertEquals(newBooking.getStart_date(), booking.getStart_date());
        assertEquals(newBooking.getEnd_date(), booking.getEnd_date());
        assertEquals(bookingsNumberBefore+1, bookingsNumberAfter);

        bookingRepository.delete(booking);
        roomRepository.delete(newRoom);
        hotelRepository.delete(newHotel);
        localizationRepository.delete(newLocalization);
        userRepository.delete(newUser);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldReturnStatus409WhileCreatingBookingWhenRoomAlreadyOccupied() throws Exception {

        //given
        Booking newBooking2 = new Booking();
        newBooking2.setUserId(newUser.getId());
        newBooking2.setRoomId(newRoom.getId());
        newBooking2.setStart_date(LocalDate.of(2023,07,11));
        newBooking2.setEnd_date(LocalDate.of(2023,07,13));

        final String jsonContentNewBooking = objectMapper.writeValueAsString(newBooking2);
        int bookingsNumberBefore = bookingRepository.findAllBookings(Pageable.unpaged()).size();

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/bookings")
                        .content(jsonContentNewBooking)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(409));

        //then
        bookingRepository.delete(newBooking);
        roomRepository.delete(newRoom);
        hotelRepository.delete(newHotel);
        localizationRepository.delete(newLocalization);
        userRepository.delete(newUser);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldReturnStatus400WhileCreatingBookingWithWrongDate() throws Exception {

        //given
        Booking newBooking2 = new Booking();
        newBooking2.setUserId(newUser.getId());
        newBooking2.setRoomId(newRoom.getId());
        newBooking2.setStart_date(LocalDate.of(2023,07,11));
        newBooking2.setEnd_date(LocalDate.of(2023,07,3));

        final String jsonContentNewBooking = objectMapper.writeValueAsString(newBooking2);
        int bookingsNumberBefore = bookingRepository.findAllBookings(Pageable.unpaged()).size();

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/bookings")
                        .content(jsonContentNewBooking)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(400));

        //then
        bookingRepository.delete(newBooking);
        roomRepository.delete(newRoom);
        hotelRepository.delete(newHotel);
        localizationRepository.delete(newLocalization);
        userRepository.delete(newUser);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldCreateBookingUser() throws Exception {

        //given
        final String jsonContentNewBooking = objectMapper.writeValueAsString(newBooking);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/bookings")
                        .content(jsonContentNewBooking)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        bookingRepository.delete(newBooking);
        roomRepository.delete(newRoom);
        hotelRepository.delete(newHotel);
        localizationRepository.delete(newLocalization);
        userRepository.delete(newUser);
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldGetSingleBooking() throws Exception {

        //given
        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/bookings/"+newBooking.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        Booking booking = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Booking.class);
        assertEquals(newBooking.getId(), booking.getId());
        assertEquals(newBooking.getUserId(), booking.getUserId());
        assertEquals(newBooking.getRoomId(), booking.getRoomId());
        assertEquals(newBooking.getStart_date(), booking.getStart_date());
        assertEquals(newBooking.getEnd_date(), booking.getEnd_date());

        bookingRepository.delete(newBooking);
        roomRepository.delete(newRoom);
        hotelRepository.delete(newHotel);
        localizationRepository.delete(newLocalization);
        userRepository.delete(newUser);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldGetSingleBookingUser() throws Exception {

        //given
        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/bookings/"+newBooking.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        bookingRepository.delete(newBooking);
        roomRepository.delete(newRoom);
        hotelRepository.delete(newHotel);
        localizationRepository.delete(newLocalization);
        userRepository.delete(newUser);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldGetMultipleBookings() throws Exception {

        //given

        Booking newBooking2 = new Booking();
        newBooking2.setUserId(newUser.getId());
        newBooking2.setRoomId(newRoom.getId());
        newBooking2.setStart_date(LocalDate.of(2023,07,15));
        newBooking2.setEnd_date(LocalDate.of(2023,07,18));
        Booking newBooking3 = new Booking();
        newBooking3.setUserId(newUser.getId());
        newBooking3.setRoomId(newRoom.getId());
        newBooking3.setStart_date(LocalDate.of(2023,07,20));
        newBooking3.setEnd_date(LocalDate.of(2023,07,23));
        bookingRepository.save(newBooking2);
        bookingRepository.save(newBooking3);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/bookings"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        Booking[] bookingsArray=objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Booking[].class);
        List<Booking> bookings =new ArrayList<>((Arrays.asList(bookingsArray)));

        assertEquals(3, bookings.size());
        assertEquals(newBooking3.getId(), bookings.get(2).getId());
        assertEquals(newBooking3.getUserId(), bookings.get(2).getUserId());
        assertEquals(newBooking3.getRoomId(), bookings.get(2).getRoomId());
        assertEquals(newBooking3.getStart_date(), bookings.get(2).getStart_date());
        assertEquals(newBooking3.getEnd_date(), bookings.get(2).getEnd_date());

        bookingRepository.delete(newBooking);
        bookingRepository.delete(newBooking2);
        bookingRepository.delete(newBooking3);
        roomRepository.delete(newRoom);
        hotelRepository.delete(newHotel);
        localizationRepository.delete(newLocalization);
        userRepository.delete(newUser);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldGetMultipleBookingsUser() throws Exception {

        //given
        Booking newBooking2 = new Booking();
        newBooking2.setUserId(newUser.getId());
        newBooking2.setRoomId(newRoom.getId());
        newBooking2.setStart_date(LocalDate.of(2023,07,15));
        newBooking2.setEnd_date(LocalDate.of(2023,07,18));
        Booking newBooking3 = new Booking();
        newBooking3.setUserId(newUser.getId());
        newBooking3.setRoomId(newRoom.getId());
        newBooking3.setStart_date(LocalDate.of(2023,07,20));
        newBooking3.setEnd_date(LocalDate.of(2023,07,23));
        bookingRepository.save(newBooking2);
        bookingRepository.save(newBooking3);

        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/bookings"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        bookingRepository.delete(newBooking);
        bookingRepository.delete(newBooking2);
        bookingRepository.delete(newBooking3);
        roomRepository.delete(newRoom);
        hotelRepository.delete(newHotel);
        localizationRepository.delete(newLocalization);
        userRepository.delete(newUser);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldEditBooking() throws Exception {

        //given
        Booking bookingEdited = new Booking();
        bookingEdited.setId(newBooking.getId());
        bookingEdited.setUserId(newUser.getId());
        bookingEdited.setRoomId(newRoom.getId());
        bookingEdited.setStart_date(LocalDate.of(2023,07,12));
        bookingEdited.setEnd_date(LocalDate.of(2023,07,15));
        final String jsonContentBookingEdited = objectMapper.writeValueAsString(bookingEdited);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/v1/bookings")
                        .content(jsonContentBookingEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        Booking bookingGet = bookingRepository.findById(bookingEdited.getId()).orElseThrow();

        //then
        Booking booking = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Booking.class);
        assertEquals(bookingEdited.getId(), booking.getId());
        assertEquals(bookingEdited.getUserId(), booking.getUserId());
        assertEquals(bookingEdited.getRoomId(), booking.getRoomId());
        assertEquals(bookingEdited.getStart_date(), booking.getStart_date());
        assertEquals(bookingEdited.getEnd_date(), booking.getEnd_date());

        bookingRepository.delete(booking);
        roomRepository.delete(newRoom);
        hotelRepository.delete(newHotel);
        localizationRepository.delete(newLocalization);
        userRepository.delete(newUser);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldReturnStatus409WhileEditingBookingWhenRoomAlreadyOccupied() throws Exception {

        //given
        Booking newBooking2 = new Booking();
        newBooking2.setUserId(newUser.getId());
        newBooking2.setRoomId(newRoom.getId());
        newBooking2.setStart_date(LocalDate.of(2023,07,15));
        newBooking2.setEnd_date(LocalDate.of(2023,07,17));
        bookingRepository.save(newBooking2);

        Booking bookingEdited = new Booking();
        bookingEdited.setId(newBooking.getId());
        bookingEdited.setUserId(newUser.getId());
        bookingEdited.setRoomId(newRoom.getId());
        bookingEdited.setStart_date(LocalDate.of(2023,07,12));
        bookingEdited.setEnd_date(LocalDate.of(2023,07,18));
        final String jsonContentBookingEdited = objectMapper.writeValueAsString(bookingEdited);

        //when
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/bookings")
                        .content(jsonContentBookingEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(409));

        //then
        bookingRepository.delete(newBooking);
        bookingRepository.delete(newBooking2);
        roomRepository.delete(newRoom);
        hotelRepository.delete(newHotel);
        localizationRepository.delete(newLocalization);
        userRepository.delete(newUser);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldEditBookingUser() throws Exception {

        //given
        Booking bookingEdited = new Booking();
        bookingEdited.setId(newBooking.getId());
        bookingEdited.setUserId(newUser.getId());
        bookingEdited.setRoomId(newRoom.getId());
        bookingEdited.setStart_date(LocalDate.of(2023,07,12));
        bookingEdited.setEnd_date(LocalDate.of(2023,07,15));
        final String jsonContentBookingEdited = objectMapper.writeValueAsString(bookingEdited);

        //when
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/bookings")
                        .content(jsonContentBookingEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        bookingRepository.delete(newBooking);
        roomRepository.delete(newRoom);
        hotelRepository.delete(newHotel);
        localizationRepository.delete(newLocalization);
        userRepository.delete(newUser);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldDeleteBooking() throws Exception {

        //given
        int bookingsNumberBefore=bookingRepository.findAllBookings(Pageable.unpaged()).size();

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/bookings/"+newBooking.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        int bookingsNumberAfter=bookingRepository.findAllBookings(Pageable.unpaged()).size();

        //then
        assertEquals(bookingsNumberBefore-1, bookingsNumberAfter);

        roomRepository.delete(newRoom);
        hotelRepository.delete(newHotel);
        localizationRepository.delete(newLocalization);
        userRepository.delete(newUser);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldDeleteBookingUser() throws Exception {

        //given
        int bookingsNumberBefore=bookingRepository.findAllBookings(Pageable.unpaged()).size();
        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/bookings/"+newBooking.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        bookingRepository.delete(newBooking);
        roomRepository.delete(newRoom);
        hotelRepository.delete(newHotel);
        localizationRepository.delete(newLocalization);
        userRepository.delete(newUser);
    }
}
