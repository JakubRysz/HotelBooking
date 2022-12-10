package com.project.hotelBooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.domain.Hotel;
import com.project.hotelBooking.domain.Localization;
import com.project.hotelBooking.domain.Room;
import com.project.hotelBooking.domain.User;
import com.project.hotelBooking.repository.HotelRepository;
import com.project.hotelBooking.repository.LocalizationRepository;
import com.project.hotelBooking.repository.RoomRepository;
import com.project.hotelBooking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class HotelBookingControllerTestSuite {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LocalizationRepository localizationRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldCreateLocalization() throws Exception {

        //given
        Localization newLocalization = new Localization();
        newLocalization.setCity("Krakow");
        newLocalization.setCountry("Poland");
        final String jsonContentNewLocalization = objectMapper.writeValueAsString(newLocalization);
        int localizationsNumberBefore = localizationRepository.findAllLocalizations(Pageable.unpaged()).size();

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/v1/localizations")
                        .content(jsonContentNewLocalization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        Localization localization = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Localization.class);
        int localizationsNumberAfter = localizationRepository.findAllLocalizations(Pageable.unpaged()).size();
        assertEquals(newLocalization.getCity(), localization.getCity());
        assertEquals(newLocalization.getCountry(), localization.getCountry());
        assertEquals(localizationsNumberBefore+1, localizationsNumberAfter);

        localizationRepository.delete(newLocalization);
    }

    @Test
    public void shouldGetSingleLocalization() throws Exception {

        //given
        Localization newLocalization = new Localization();
        newLocalization.setCity("Krakow");
        newLocalization.setCountry("Poland");
        localizationRepository.save(newLocalization);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/localizations/Hotels/"+newLocalization.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        Localization localization = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Localization.class);

        assertEquals(newLocalization.getId(), localization.getId());
        assertEquals(newLocalization.getCity(), localization.getCity());
        assertEquals(newLocalization.getCountry(), localization.getCountry());

        localizationRepository.delete(newLocalization);
    }

    @Test
    public void shouldGetMultipleLocalizations() throws Exception {

        //given
        Localization newLocalization1 = new Localization();
        newLocalization1.setCity("Krakow");
        newLocalization1.setCountry("Poland");
        Localization newLocalization2 = new Localization();
        newLocalization2.setCity("Poznan");
        newLocalization2.setCountry("Poland");
        Localization newLocalization3 = new Localization();
        newLocalization3.setCity("Warsaw");
        newLocalization3.setCountry("Poland");
        localizationRepository.save(newLocalization1);
        localizationRepository.save(newLocalization2);
        localizationRepository.save(newLocalization3);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/localizations/Hotels/"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        Localization[] localizationsArray=objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Localization[].class);
        List<Localization> localizations =new ArrayList<>((Arrays.asList(localizationsArray)));

        assertEquals(3, localizations.size());
        assertEquals(newLocalization3.getId(), localizations.get(2).getId());
        assertEquals(newLocalization3.getCity(), localizations.get(2).getCity());
        assertEquals(newLocalization3.getCountry(), localizations.get(2).getCountry());

        localizationRepository.delete(newLocalization1);
        localizationRepository.delete(newLocalization2);
        localizationRepository.delete(newLocalization3);
    }

    @Test
    public void shouldEditLocalization() throws Exception {

        //given
        Localization newLocalization = new Localization();
        newLocalization.setCity("Krakow");
        newLocalization.setCountry("Poland");
        localizationRepository.save(newLocalization);

        Localization localizationEdited = new Localization();
        localizationEdited.setId(newLocalization.getId());
        localizationEdited.setCity("Gdansk");
        localizationEdited.setCountry("Poland");
        final String jsonContentLocalizationEdited = objectMapper.writeValueAsString(localizationEdited);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/v1/localizations")
                        .content(jsonContentLocalizationEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        Localization localizationGet = localizationRepository.findById(localizationEdited.getId()).orElseThrow();

        //then
        Localization localization = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Localization.class);
        assertEquals(localizationEdited.getId(), localizationGet.getId());
        assertEquals(localizationEdited.getCity(), localizationGet.getCity());
        assertEquals(localizationEdited.getCountry(), localizationGet.getCountry());

        localizationRepository.delete(localization);
    }
    @Test
    public void shouldDeleteLocalization() throws Exception {

        //given
        Localization newLocalization = new Localization();
        newLocalization.setCity("Krakow");
        newLocalization.setCountry("Poland");
        localizationRepository.save(newLocalization);
        int localizationsNumberBefore=localizationRepository.findAllLocalizations(Pageable.unpaged()).size();

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/localizations/"+newLocalization.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        int localizationsNumberAfter=localizationRepository.findAllLocalizations(Pageable.unpaged()).size();

        //then
        assertEquals(localizationsNumberBefore-1, localizationsNumberAfter);
    }

    @Test
    public void shouldCreateHotel() throws Exception {

        //given
        Localization newLocalization = new Localization();
        newLocalization.setCity("Krakow");
        newLocalization.setCountry("Poland");
        localizationRepository.save(newLocalization);

        Hotel newHotel = new Hotel();
        newHotel.setName("Hilton1");
        newHotel.setNumberOfStars(3);
        newHotel.setHotelChain("Hilton");
        newHotel.setLocalizationId(newLocalization.getId());

        final String jsonContentNewHotel = objectMapper.writeValueAsString(newHotel);
        int hotelsNumberBefore = hotelRepository.findAllHotels(Pageable.unpaged()).size();

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/v1/hotels")
                        .content(jsonContentNewHotel)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        Hotel hotel = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Hotel.class);
        int hotelsNumberAfter = hotelRepository.findAllHotels(Pageable.unpaged()).size();
        assertEquals(newHotel.getName(), hotel.getName());
        assertEquals(newHotel.getNumberOfStars(), hotel.getNumberOfStars());
        assertEquals(newHotel.getHotelChain(), hotel.getHotelChain());
        assertEquals(newHotel.getLocalizationId(), hotel.getLocalizationId());
        assertEquals(hotelsNumberBefore+1, hotelsNumberAfter);

        hotelRepository.delete(hotel);
        localizationRepository.delete(newLocalization);
    }

    @Test
    public void shouldGetSingleHotel() throws Exception {

        //given
        Localization newLocalization = new Localization();
        newLocalization.setCity("Krakow");
        newLocalization.setCountry("Poland");
        localizationRepository.save(newLocalization);

        Hotel newHotel = new Hotel();
        newHotel.setName("Hilton1");
        newHotel.setNumberOfStars(3);
        newHotel.setHotelChain("Hilton");
        newHotel.setLocalizationId(newLocalization.getId());
        hotelRepository.save(newHotel);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/hotels/Rooms/"+newHotel.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        Hotel hotel = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Hotel.class);

        assertEquals(newHotel.getId(), hotel.getId());
        assertEquals(newHotel.getName(), hotel.getName());
        assertEquals(newHotel.getNumberOfStars(), hotel.getNumberOfStars());
        assertEquals(newHotel.getHotelChain(), hotel.getHotelChain());
        assertEquals(newHotel.getLocalizationId(), hotel.getLocalizationId());

        hotelRepository.delete(newHotel);
        localizationRepository.delete(newLocalization);
    }

    @Test
    public void shouldGetMultipleHotels() throws Exception {

        //given
        Localization newLocalization = new Localization();
        newLocalization.setCity("Krakow");
        newLocalization.setCountry("Poland");
        localizationRepository.save(newLocalization);

        Hotel newHotel1 = new Hotel();
        newHotel1.setName("Hilton1");
        newHotel1.setNumberOfStars(3);
        newHotel1.setHotelChain("Hilton");
        Hotel newHotel2 = new Hotel();
        newHotel2.setName("Hilton2");
        newHotel2.setNumberOfStars(4);
        newHotel2.setHotelChain("Hilton");
        Hotel newHotel3 = new Hotel();
        newHotel3.setName("Hilton3");
        newHotel3.setNumberOfStars(5);
        newHotel3.setHotelChain("Hilton");
;
        hotelRepository.save(newHotel1);
        hotelRepository.save(newHotel2);
        hotelRepository.save(newHotel3);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/hotels/Rooms"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        Hotel[] hotelsArray=objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Hotel[].class);
        List<Hotel> hotels =new ArrayList<>((Arrays.asList(hotelsArray)));

        assertEquals(3, hotels.size());
        assertEquals(newHotel3.getId(), hotels.get(2).getId());
        assertEquals(newHotel3.getName(), hotels.get(2).getName());
        assertEquals(newHotel3.getNumberOfStars(), hotels.get(2).getNumberOfStars());
        assertEquals(newHotel3.getHotelChain(), hotels.get(2).getHotelChain());
        assertEquals(newHotel3.getLocalizationId(), hotels.get(2).getLocalizationId());

        hotelRepository.delete(newHotel1);
        hotelRepository.delete(newHotel2);
        hotelRepository.delete(newHotel3);
        localizationRepository.delete(newLocalization);
    }

    @Test
    public void shouldEditHotel() throws Exception {

        //given
        Localization newLocalization = new Localization();
        newLocalization.setCity("Krakow");
        newLocalization.setCountry("Poland");
        localizationRepository.save(newLocalization);

        Hotel newHotel = new Hotel();
        newHotel.setName("Hilton1");
        newHotel.setNumberOfStars(3);
        newHotel.setHotelChain("Hilton");
        newHotel.setLocalizationId(newLocalization.getId());
        hotelRepository.save(newHotel);

        Hotel hotelEdited = new Hotel();
        hotelEdited.setId(newHotel.getId());
        hotelEdited.setName("Hilton1");
        hotelEdited.setNumberOfStars(5);
        hotelEdited.setHotelChain("Hilton");
        hotelEdited.setLocalizationId(newLocalization.getId());
        final String jsonContentHotelEdited = objectMapper.writeValueAsString(hotelEdited);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/v1/hotels")
                        .content(jsonContentHotelEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        Hotel hotelGet = hotelRepository.findById(hotelEdited.getId()).orElseThrow();

        //then
        Hotel hotel = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Hotel.class);
        assertEquals(hotelEdited.getId(), hotel.getId());
        assertEquals(hotelEdited.getName(), hotel.getName());
        assertEquals(hotelEdited.getNumberOfStars(), hotel.getNumberOfStars());
        assertEquals(hotelEdited.getHotelChain(), hotel.getHotelChain());
        assertEquals(hotelEdited.getLocalizationId(), hotel.getLocalizationId());

        hotelRepository.delete(hotel);
        localizationRepository.delete(newLocalization);
    }

    @Test
    public void shouldDeleteHotel() throws Exception {

        //given
        Localization newLocalization = new Localization();
        newLocalization.setCity("Krakow");
        newLocalization.setCountry("Poland");
        localizationRepository.save(newLocalization);

        Hotel newHotel = new Hotel();
        newHotel.setName("Hilton1");
        newHotel.setNumberOfStars(3);
        newHotel.setHotelChain("Hilton");
        newHotel.setLocalizationId(newLocalization.getId());
        hotelRepository.save(newHotel);
        int hotelsNumberBefore=hotelRepository.findAllHotels(Pageable.unpaged()).size();

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/hotels/"+newHotel.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        int hotelsNumberAfter=hotelRepository.findAllHotels(Pageable.unpaged()).size();

        //then
        assertEquals(hotelsNumberBefore-1, hotelsNumberAfter);

        localizationRepository.delete(newLocalization);
    }

    @Test
    public void shouldCreateRoom() throws Exception {

        //given
        Hotel newHotel = new Hotel();
        newHotel.setName("Hilton1");
        newHotel.setNumberOfStars(3);
        newHotel.setHotelChain("Hilton");
        hotelRepository.save(newHotel);

        Room newRoom = new Room();
        newRoom.setRoomNumber(15);
        newRoom.setNumberOfPersons(2);
        newRoom.setStandard(4);
        newRoom.setHotelId(newHotel.getId());

        final String jsonContentNewRoom = objectMapper.writeValueAsString(newRoom);
        int roomsNumberBefore = roomRepository.findAllRooms(Pageable.unpaged()).size();

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/v1/rooms")
                        .content(jsonContentNewRoom)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        Room room = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Room.class);
        int roomsNumberAfter = roomRepository.findAllRooms(Pageable.unpaged()).size();
        assertEquals(newRoom.getRoomNumber(), room.getRoomNumber());
        assertEquals(newRoom.getNumberOfPersons(), room.getNumberOfPersons());
        assertEquals(newRoom.getStandard(), room.getStandard());
        assertEquals(newRoom.getHotelId(), room.getHotelId());
        assertEquals(roomsNumberBefore+1, roomsNumberAfter);

        roomRepository.delete(room);
        hotelRepository.delete(newHotel);
    }

    @Test
    public void shouldGetSingleRoom() throws Exception {

        //given
        Hotel newHotel = new Hotel();
        newHotel.setName("Hilton1");
        newHotel.setNumberOfStars(3);
        newHotel.setHotelChain("Hilton");
        hotelRepository.save(newHotel);

        Room newRoom = new Room();
        newRoom.setRoomNumber(15);
        newRoom.setNumberOfPersons(2);
        newRoom.setStandard(4);
        newRoom.setHotelId(newHotel.getId());
        roomRepository.save(newRoom);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/rooms/Bookings/"+newRoom.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        Room room = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Room.class);

        assertEquals(newRoom.getId(), room.getId());
        assertEquals(newRoom.getRoomNumber(), room.getRoomNumber());
        assertEquals(newRoom.getNumberOfPersons(), room.getNumberOfPersons());
        assertEquals(newRoom.getStandard(), room.getStandard());
        assertEquals(newRoom.getHotelId(), room.getHotelId());

        roomRepository.delete(newRoom);
        hotelRepository.delete(newHotel);
    }

    @Test
    public void shouldGetMultipleRooms() throws Exception {

        //given
        Hotel newHotel = new Hotel();
        newHotel.setName("Hilton1");
        newHotel.setNumberOfStars(3);
        newHotel.setHotelChain("Hilton");
        hotelRepository.save(newHotel);

        Room newRoom1 = new Room();
        newRoom1.setRoomNumber(15);
        newRoom1.setNumberOfPersons(2);
        newRoom1.setStandard(4);
        newRoom1.setHotelId(newHotel.getId());
        Room newRoom2 = new Room();
        newRoom2.setRoomNumber(16);
        newRoom2.setNumberOfPersons(3);
        newRoom2.setStandard(3);
        newRoom2.setHotelId(newHotel.getId());
        Room newRoom3 = new Room();
        newRoom3.setRoomNumber(17);
        newRoom3.setNumberOfPersons(1);
        newRoom3.setStandard(5);
        newRoom3.setHotelId(newHotel.getId());

        roomRepository.save(newRoom1);
        roomRepository.save(newRoom2);
        roomRepository.save(newRoom3);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/rooms/Bookings"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        Room[] roomsArray=objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Room[].class);
        List<Room> rooms =new ArrayList<>((Arrays.asList(roomsArray)));

        assertEquals(3, rooms.size());
        assertEquals(newRoom3.getId(), rooms.get(2).getId());
        assertEquals(newRoom3.getRoomNumber(), rooms.get(2).getRoomNumber());
        assertEquals(newRoom3.getNumberOfPersons(), rooms.get(2).getNumberOfPersons());
        assertEquals(newRoom3.getStandard(), rooms.get(2).getStandard());
        assertEquals(newRoom3.getHotelId(), rooms.get(2).getHotelId());

        roomRepository.delete(newRoom1);
        roomRepository.delete(newRoom2);
        roomRepository.delete(newRoom3);
        hotelRepository.delete(newHotel);
    }

    @Test
    public void shouldEditRoom() throws Exception {

        //given
        Hotel newHotel = new Hotel();
        newHotel.setName("Hilton1");
        newHotel.setNumberOfStars(3);
        newHotel.setHotelChain("Hilton");
        hotelRepository.save(newHotel);

        Room newRoom = new Room();
        newRoom.setRoomNumber(15);
        newRoom.setNumberOfPersons(2);
        newRoom.setStandard(4);
        newRoom.setHotelId(newHotel.getId());
        roomRepository.save(newRoom);

        Room roomEdited = new Room();
        roomEdited.setId(newRoom.getId());
        roomEdited.setRoomNumber(20);
        roomEdited.setNumberOfPersons(2);
        roomEdited.setStandard(4);
        roomEdited.setHotelId(newHotel.getId());
        final String jsonContentRoomEdited = objectMapper.writeValueAsString(roomEdited);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/v1/rooms")
                        .content(jsonContentRoomEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        Room roomGet = roomRepository.findById(roomEdited.getId()).orElseThrow();

        //then
        Room room = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Room.class);
        assertEquals(roomEdited.getId(), room.getId());
        assertEquals(roomEdited.getRoomNumber(), room.getRoomNumber());
        assertEquals(roomEdited.getNumberOfPersons(), room.getNumberOfPersons());
        assertEquals(roomEdited.getStandard(), room.getStandard());
        assertEquals(roomEdited.getHotelId(), room.getHotelId());

        roomRepository.delete(room);
        hotelRepository.delete(newHotel);
    }

    @Test
    public void shouldDeleteRoom() throws Exception {

        //given
        Hotel newHotel = new Hotel();
        newHotel.setName("Hilton1");
        newHotel.setNumberOfStars(3);
        newHotel.setHotelChain("Hilton");
        hotelRepository.save(newHotel);

        Room newRoom = new Room();
        newRoom.setRoomNumber(15);
        newRoom.setNumberOfPersons(2);
        newRoom.setStandard(4);
        newRoom.setHotelId(newHotel.getId());
        roomRepository.save(newRoom);
        int roomsNumberBefore=roomRepository.findAllRooms(Pageable.unpaged()).size();

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/rooms/"+newRoom.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        int roomsNumberAfter=roomRepository.findAllRooms(Pageable.unpaged()).size();

        //then
        assertEquals(roomsNumberBefore-1, roomsNumberAfter);
    }

    @Test
    public void shouldCreateUser() throws Exception {

        //given
        User newUser = new User();
        newUser.setFirstName("Poul");
        newUser.setLastName("Smith");
        newUser.setDateOfBirth(LocalDate.of(1991,2,16));
        final String jsonContentNewUser = objectMapper.writeValueAsString(newUser);
        int usersNumberBefore = userRepository.findAllUsers(Pageable.unpaged()).size();

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/v1/users")
                        .content(jsonContentNewUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        User user = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);
        int usersNumberAfter = userRepository.findAllUsers(Pageable.unpaged()).size();
        assertEquals(newUser.getFirstName(), user.getFirstName());
        assertEquals(newUser.getLastName(), user.getLastName());
        assertEquals(newUser.getDateOfBirth(), user.getDateOfBirth());
        assertEquals(usersNumberBefore+1, usersNumberAfter);

        userRepository.delete(newUser);
    }

    @Test
    public void shouldGetSingleUser() throws Exception {

        //given
        User newUser = new User();
        newUser.setFirstName("Poul");
        newUser.setLastName("Smith");
        newUser.setDateOfBirth(LocalDate.of(1991,2,16));
        userRepository.save(newUser);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/Bookings/"+newUser.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        User user = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);

        assertEquals(newUser.getId(), user.getId());
        assertEquals(newUser.getFirstName(), user.getFirstName());
        assertEquals(newUser.getLastName(), user.getLastName());
        assertEquals(newUser.getDateOfBirth(), user.getDateOfBirth());

        userRepository.delete(newUser);
    }

    @Test
    public void shouldGetMultipleUsers() throws Exception {

        //given
        User newUser1 = new User();
        newUser1.setFirstName("Poul");
        newUser1.setLastName("Smith");
        newUser1.setDateOfBirth(LocalDate.of(1991,2,15));
        User newUser2 = new User();
        newUser2.setFirstName("Jan");
        newUser2.setLastName("Kowalski");
        newUser2.setDateOfBirth(LocalDate.of(1992,3,15));
        User newUser3 = new User();
        newUser3.setFirstName("Cris");
        newUser3.setLastName("Brown");
        newUser3.setDateOfBirth(LocalDate.of(1993,4,15));
        userRepository.save(newUser1);
        userRepository.save(newUser2);
        userRepository.save(newUser3);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/Bookings/"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        User[] usersArray=objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User[].class);
        List<User> users =new ArrayList<>((Arrays.asList(usersArray)));

        assertEquals(3, users.size());
        assertEquals(newUser3.getId(), users.get(2).getId());
        assertEquals(newUser3.getFirstName(), users.get(2).getFirstName());
        assertEquals(newUser3.getLastName(), users.get(2).getLastName());
        assertEquals(newUser3.getDateOfBirth(), users.get(2).getDateOfBirth());

        userRepository.delete(newUser1);
        userRepository.delete(newUser2);
        userRepository.delete(newUser3);
    }

    @Test
    public void shouldEditUser() throws Exception {

        //given
        User newUser = new User();
        newUser.setFirstName("Poul");
        newUser.setLastName("Smith");
        newUser.setDateOfBirth(LocalDate.of(1991,2,16));
        userRepository.save(newUser);

        User userEdited = new User();
        userEdited.setId(newUser.getId());
        userEdited.setFirstName("Poul");
        userEdited.setLastName("Smith");
        userEdited.setDateOfBirth(LocalDate.of(1982,2,16));
        final String jsonContentUserEdited = objectMapper.writeValueAsString(userEdited);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/v1/users")
                        .content(jsonContentUserEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        User userGet = userRepository.findById(userEdited.getId()).orElseThrow();

        //then
        User user = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);
        assertEquals(userEdited.getId(), user.getId());
        assertEquals(userEdited.getFirstName(), user.getFirstName());
        assertEquals(userEdited.getLastName(), user.getLastName());
        assertEquals(userEdited.getDateOfBirth(), user.getDateOfBirth());

        userRepository.delete(user);
    }

    @Test
    public void shouldDeleteUser() throws Exception {

        //given
        User newUser = new User();
        newUser.setFirstName("Poul");
        newUser.setLastName("Smith");
        newUser.setDateOfBirth(LocalDate.of(1991,2,16));
        userRepository.save(newUser);
        int usersNumberBefore=userRepository.findAllUsers(Pageable.unpaged()).size();

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/users/"+newUser.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        int usersNumberAfter=userRepository.findAllUsers(Pageable.unpaged()).size();

        //then
        assertEquals(usersNumberBefore-1, usersNumberAfter);
    }

}