package com.project.hotelBooking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.common.CommonDatabaseUtils;
import com.project.hotelBooking.controller.mapper.RoomMapper;
import com.project.hotelBooking.controller.mapper.UserMapper;
import com.project.hotelBooking.controller.model.RoomDto;
import com.project.hotelBooking.controller.model.UserDto;
import com.project.hotelBooking.repository.HotelRepository;
import com.project.hotelBooking.repository.LocalizationRepository;
import com.project.hotelBooking.repository.RoomRepository;
import com.project.hotelBooking.repository.model.Hotel;
import com.project.hotelBooking.repository.model.Localization;
import com.project.hotelBooking.repository.model.Room;
import com.project.hotelBooking.repository.model.User;
import com.project.hotelBooking.service.mapper.RoomMapperServ;
import com.project.hotelBooking.service.mapper.UserMapperServ;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoomControllerE2ETest {

    public static final String ROOMS_URL = "/v1/rooms/";
    private static final String ROOMS_BOOKINGS_URL = ROOMS_URL + "bookings/";
    private static final String ROOMS_BOOKINGS_WITHOUT_USERS_URL = ROOMS_BOOKINGS_URL + "withoutUsers/";
    private final ObjectMapper objectMapper;
    private final LocalizationRepository localizationRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final MockMvc mockMvc;
    private final RoomMapper roomMapper;
    private final RoomMapperServ roomMapperServ;
    private final CommonDatabaseUtils commonDatabaseUtils;

    private Hotel hotel1;
    private Localization localization1;

    @BeforeEach
    public void initialize() {
        localization1 = localizationRepository.save(LOCALIZATION_1);
        hotel1 = hotelRepository.save(getHotel1(localization1.getId()));
    }

    @AfterEach
    public void clear() {
        commonDatabaseUtils.clearDatabaseTables();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldCreateRoom() throws Exception {
        //given
        Room room1 = getRoom1(hotel1.getId());
        RoomDto room1Dto = mapRoomToRoomDto(room1);
        final String jsonContentNewRoom = objectMapper.writeValueAsString(room1Dto);
        int roomsNumberBefore = roomRepository.findAllRooms(Pageable.unpaged()).size();

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(ROOMS_URL)
                        .content(jsonContentNewRoom)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        RoomDto room = getRoomFromResponse(mvcResult);
        int roomsNumberAfter = roomRepository.findAllRooms(Pageable.unpaged()).size();
        Room roomFromDatabase = roomRepository.findById(room.getId()).orElseThrow();
        assertEqualsRoomsWithoutId(room1, roomFromDatabase);
        assertEquals(roomsNumberBefore + 1, roomsNumberAfter);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403CreateRoom_user() throws Exception {
        //given
        Room room1 = getRoom1(hotel1.getId());
        RoomDto room1Dto = mapRoomToRoomDto(room1);
        final String jsonContentNewRoom = objectMapper.writeValueAsString(room1Dto);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post(ROOMS_URL)
                        .content(jsonContentNewRoom)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldGetSingleRoom() throws Exception {
        //given
        Room room1 = getRoom1(hotel1.getId());
        Room roomSaved = roomRepository.save(room1);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ROOMS_BOOKINGS_URL + roomSaved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        RoomDto room = getRoomFromResponse(mvcResult);
        assertEqualsRoomsWithoutId(roomSaved, mapRoomDtoToRoom(room));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403GetSingleRoom_user() throws Exception {
        //given
        Room room1 = getRoom1(hotel1.getId());
        Room roomSaved = roomRepository.save(room1);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get(ROOMS_BOOKINGS_URL + roomSaved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldGetSingleRoom_withBookings_withoutUsers_user() throws Exception {
        //given
        Room room1 = getRoom1(hotel1.getId());
        Room roomSaved = roomRepository.save(room1);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ROOMS_BOOKINGS_WITHOUT_USERS_URL + roomSaved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        RoomDto room = getRoomFromResponse(mvcResult);
        assertEqualsRooms(roomSaved, mapRoomDtoToRoom(room));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldGetMultipleRooms() throws Exception {
        //given
        Room room2 = Room.builder()
                .roomNumber(16)
                .numberOfPersons(3)
                .standard(3)
                .hotelId(hotel1.getId())
                .build();

        Room room3 = Room.builder()
                .roomNumber(17)
                .numberOfPersons(1)
                .standard(5)
                .hotelId(hotel1.getId())
                .build();

        Room room1Saved = roomRepository.save(getRoom1(hotel1.getId()));
        Room room2Saved = roomRepository.save(room2);
        Room room3Saved = roomRepository.save(room3);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ROOMS_BOOKINGS_URL))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        RoomDto[] roomsArray = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), RoomDto[].class);
        List<RoomDto> rooms = new ArrayList<>((Arrays.asList(roomsArray)));

        assertEquals(3, rooms.size());
        assertEqualsRooms(room1Saved, mapRoomDtoToRoom(rooms.get(0)));
        assertEqualsRooms(room2Saved, mapRoomDtoToRoom(rooms.get(1)));
        assertEqualsRooms(room3Saved, mapRoomDtoToRoom(rooms.get(2)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403GetMultipleRooms_user() throws Exception {
        //given;
        Room room2 = Room.builder()
                .roomNumber(16)
                .numberOfPersons(3)
                .standard(3)
                .hotelId(hotel1.getId())
                .build();

        Room room3 = Room.builder()
                .roomNumber(17)
                .numberOfPersons(1)
                .standard(5)
                .hotelId(hotel1.getId())
                .build();

        roomRepository.save(getRoom1(hotel1.getId()));
        roomRepository.save(room2);
        roomRepository.save(room3);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get(ROOMS_BOOKINGS_URL))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldGetMultipleRooms_withBookingsWithoutUsers_user() throws Exception {
        //given
        Room room2 = Room.builder()
                .roomNumber(16)
                .numberOfPersons(3)
                .standard(3)
                .hotelId(hotel1.getId())
                .build();

        Room room3 = Room.builder()
                .roomNumber(17)
                .numberOfPersons(1)
                .standard(5)
                .hotelId(hotel1.getId())
                .build();

        Room room1Saved = roomRepository.save(getRoom1(hotel1.getId()));
        Room room2Saved = roomRepository.save(room2);
        Room room3Saved = roomRepository.save(room3);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ROOMS_BOOKINGS_WITHOUT_USERS_URL))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        RoomDto[] roomsArray = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), RoomDto[].class);
        List<RoomDto> rooms = new ArrayList<>((Arrays.asList(roomsArray)));

        assertEquals(3, rooms.size());
        assertEqualsRooms(room1Saved, mapRoomDtoToRoom(rooms.get(0)));
        assertEqualsRooms(room2Saved, mapRoomDtoToRoom(rooms.get(1)));
        assertEqualsRooms(room3Saved, mapRoomDtoToRoom(rooms.get(2)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldEditRoom() throws Exception {
        //given
        Room room1Saved = roomRepository.save(getRoom1(hotel1.getId()));

        RoomDto roomEdited = RoomDto.builder()
                .id(room1Saved.getId())
                .roomNumber(room1Saved.getRoomNumber())
                .numberOfPersons(room1Saved.getNumberOfPersons() + 1)
                .standard(room1Saved.getStandard())
                .hotelId(room1Saved.getHotelId())
                .build();

        final String jsonContentRoomEdited = objectMapper.writeValueAsString(roomEdited);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(ROOMS_URL)
                        .content(jsonContentRoomEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        RoomDto room = getRoomFromResponse(mvcResult);
        Room roomFromDatabase = roomRepository.findById(room.getId()).orElseThrow();
        assertEqualsRooms(mapRoomDtoToRoom(roomEdited), roomFromDatabase);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403EditRoom_user() throws Exception {
        //given
        Room room1Saved = roomRepository.save(getRoom1(hotel1.getId()));

        RoomDto roomEdited = RoomDto.builder()
                .roomNumber(room1Saved.getRoomNumber())
                .numberOfPersons(room1Saved.getNumberOfPersons() + 1)
                .standard(room1Saved.getStandard())
                .hotelId(room1Saved.getHotelId())
                .build();

        final String jsonContentRoomEdited = objectMapper.writeValueAsString(roomEdited);

        //when
        mockMvc.perform(MockMvcRequestBuilders.put(ROOMS_URL)
                        .content(jsonContentRoomEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldDeleteRoom() throws Exception {
        //given
        Room room1 = getRoom1(hotel1.getId());
        Room roomSaved = roomRepository.save(room1);
        int roomsNumberBefore = roomRepository.findAllRooms(Pageable.unpaged()).size();

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete(ROOMS_URL + roomSaved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        int roomsNumberAfter = roomRepository.findAllRooms(Pageable.unpaged()).size();

        //then
        assertEquals(roomsNumberBefore - 1, roomsNumberAfter);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403DeleteRoom_user() throws Exception {
        //given
        Room room1 = getRoom1(hotel1.getId());
        Room roomSaved = roomRepository.save(room1);

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete(ROOMS_URL + roomSaved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

    private static void assertEqualsRoomsWithoutId(Room expedRoom, Room actualRoom) {
        assertEquals(expedRoom.getRoomNumber(), actualRoom.getRoomNumber());
        assertEquals(expedRoom.getNumberOfPersons(), actualRoom.getNumberOfPersons());
        assertEquals(expedRoom.getStandard(), actualRoom.getStandard());
        assertEquals(expedRoom.getHotelId(), actualRoom.getHotelId());
    }

    private static void assertEqualsRooms(Room expedRoom, Room actualRoom) {
        assertEquals(expedRoom.getId(), actualRoom.getId());
        assertEqualsRoomsWithoutId(expedRoom, actualRoom);
    }

    private RoomDto getRoomFromResponse(MvcResult mvcResult) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), RoomDto.class);
    }

    private RoomDto mapRoomToRoomDto(Room room) {
        return roomMapper.mapToRoomDto(roomMapperServ.mapToRoom(room));
    }

    private Room mapRoomDtoToRoom(RoomDto roomDto) {
        return roomMapperServ.mapToRepositoryRoom(roomMapper.mapToRoom(roomDto));
    }
}