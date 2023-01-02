package com.project.hotelBooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.domain.*;
import com.project.hotelBooking.repository.*;
import lombok.RequiredArgsConstructor;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoomControllerTestSuite {

    private final ObjectMapper objectMapper;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final MockMvc mockMvc;

    @Test
    @WithMockUser(roles = {"ADMIN"})
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
        assertEquals(roomsNumberBefore + 1, roomsNumberAfter);

        roomRepository.delete(room);
        hotelRepository.delete(newHotel);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldCreateRoomUser() throws Exception {

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

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/rooms")
                        .content(jsonContentNewRoom)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        roomRepository.delete(newRoom);
        hotelRepository.delete(newHotel);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
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
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/rooms/Bookings/" + newRoom.getId()))
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
    @WithMockUser(roles = {"USER"})
    public void shouldGetSingleRoomUser() throws Exception {

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

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/rooms/Bookings/" + newRoom.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        roomRepository.delete(newRoom);
        hotelRepository.delete(newHotel);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldGetSingleRoomWithoutUsersUser() throws Exception {

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
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/rooms/Bookings/WithoutUsers/" + newRoom.getId()))
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
    @WithMockUser(roles = {"ADMIN"})
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
        Room[] roomsArray = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Room[].class);
        List<Room> rooms = new ArrayList<>((Arrays.asList(roomsArray)));

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
    @WithMockUser(roles = {"USER"})
    public void shouldGetMultipleRoomsUser() throws Exception {

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

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/rooms/Bookings"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        roomRepository.delete(newRoom1);
        roomRepository.delete(newRoom2);
        roomRepository.delete(newRoom3);
        hotelRepository.delete(newHotel);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldGetMultipleRoomsWithoutUsersUser() throws Exception {

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
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/rooms/Bookings/WithoutUsers"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        Room[] roomsArray = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Room[].class);
        List<Room> rooms = new ArrayList<>((Arrays.asList(roomsArray)));

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
    @WithMockUser(roles = {"ADMIN"})
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
    @WithMockUser(roles = {"USER"})
    public void shouldEditRoomUser() throws Exception {

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
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/rooms")
                        .content(jsonContentRoomEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        roomRepository.delete(newRoom);
        hotelRepository.delete(newHotel);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
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
        int roomsNumberBefore = roomRepository.findAllRooms(Pageable.unpaged()).size();

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/rooms/" + newRoom.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        int roomsNumberAfter = roomRepository.findAllRooms(Pageable.unpaged()).size();

        //then
        assertEquals(roomsNumberBefore - 1, roomsNumberAfter);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldDeleteRoomUser() throws Exception {

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
        int roomsNumberBefore = roomRepository.findAllRooms(Pageable.unpaged()).size();

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/rooms/" + newRoom.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        roomRepository.delete(newRoom);
        hotelRepository.delete(newHotel);
    }
}