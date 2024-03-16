package com.project.hotelBooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.repository.model.Hotel;
import com.project.hotelBooking.repository.model.Localization;
import com.project.hotelBooking.repository.HotelRepository;
import com.project.hotelBooking.repository.LocalizationRepository;
import lombok.RequiredArgsConstructor;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
    public class HotelControllerE2ETest {

    private final ObjectMapper objectMapper;
    private final LocalizationRepository localizationRepository;
    private final HotelRepository hotelRepository;
    private final MockMvc mockMvc;
    private final Localization newLocalization = new Localization();
    private Hotel newHotel;

    @BeforeEach
    public void initialize() {
        //given
        newLocalization.setCity("Cracow");
        newLocalization.setCountry("Poland");
        localizationRepository.save(newLocalization);

        newHotel = Hotel.builder()
                .name("Hilton1")
                .numberOfStars(3)
                .hotelChain("Hilton")
                .localizationId(newLocalization.getId())
                .build();

        hotelRepository.save(newHotel);
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldCreateHotel() throws Exception {

        //given
        hotelRepository.delete(newHotel);
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
        assertEquals(hotelsNumberBefore + 1, hotelsNumberAfter);

        hotelRepository.delete(hotel);
        localizationRepository.delete(newLocalization);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403CreateHotelUser() throws Exception {

        //given
        final String jsonContentNewHotel = objectMapper.writeValueAsString(newHotel);

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/hotels")
                        .content(jsonContentNewHotel)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        hotelRepository.delete(newHotel);
        localizationRepository.delete(newLocalization);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldGetSingleHotel() throws Exception {

        //given
        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/hotels/rooms/" + newHotel.getId()))
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
    @WithMockUser(roles = {"USER"})
    public void shouldGetMultipleHotels() throws Exception {

        //given
        Hotel newHotel2 = Hotel.builder()
                .name("Hilton2")
                .numberOfStars(4)
                .hotelChain("Hilton")
                .build();

        Hotel newHotel3 = Hotel.builder()
                .name("Hilton3")
                .numberOfStars(5)
                .hotelChain("Hilton")
                .build();

        hotelRepository.save(newHotel2);
        hotelRepository.save(newHotel3);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/hotels/rooms"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        Hotel[] hotelsArray = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Hotel[].class);
        List<Hotel> hotels = new ArrayList<>((Arrays.asList(hotelsArray)));

        assertEquals(3, hotels.size());
        assertEquals(newHotel3.getId(), hotels.get(2).getId());
        assertEquals(newHotel3.getName(), hotels.get(2).getName());
        assertEquals(newHotel3.getNumberOfStars(), hotels.get(2).getNumberOfStars());
        assertEquals(newHotel3.getHotelChain(), hotels.get(2).getHotelChain());
        assertEquals(newHotel3.getLocalizationId(), hotels.get(2).getLocalizationId());

        hotelRepository.delete(newHotel);
        hotelRepository.delete(newHotel2);
        hotelRepository.delete(newHotel3);
        localizationRepository.delete(newLocalization);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldEditHotel() throws Exception {

        //given
        Hotel hotelEdited = Hotel.builder()
                .id(newHotel.getId())
                .name("Hilton1")
                .numberOfStars(5)
                .hotelChain("Hilton")
                .localizationId(newLocalization.getId())
                .build();

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
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403EditHotelUser() throws Exception {

        //given

        Hotel hotelEdited = Hotel.builder()
                .id(newHotel.getId())
                .name("Hilton1")
                .numberOfStars(5)
                .hotelChain("Hilton")
                .localizationId(newLocalization.getId())
                .build();

        final String jsonContentHotelEdited = objectMapper.writeValueAsString(hotelEdited);

        //when
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/hotels")
                        .content(jsonContentHotelEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        hotelRepository.delete(newHotel);
        localizationRepository.delete(newLocalization);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldDeleteHotel() throws Exception {

        //given
        int hotelsNumberBefore = hotelRepository.findAllHotels(Pageable.unpaged()).size();

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/hotels/" + newHotel.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        int hotelsNumberAfter = hotelRepository.findAllHotels(Pageable.unpaged()).size();

        //then
        assertEquals(hotelsNumberBefore - 1, hotelsNumberAfter);

        localizationRepository.delete(newLocalization);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403DeleteHotelUser() throws Exception {

        //given

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/hotels/" + newHotel.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        hotelRepository.delete(newHotel);
        localizationRepository.delete(newLocalization);
    }
}