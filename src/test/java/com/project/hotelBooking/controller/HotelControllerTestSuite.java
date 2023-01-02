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
public class HotelControllerTestSuite {

    private final ObjectMapper objectMapper;
    private final LocalizationRepository localizationRepository;
    private final HotelRepository hotelRepository;
    private final MockMvc mockMvc;

    @Test
    @WithMockUser(roles = {"ADMIN"})
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
        assertEquals(hotelsNumberBefore + 1, hotelsNumberAfter);

        hotelRepository.delete(hotel);
        localizationRepository.delete(newLocalization);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldCreateHotelUser() throws Exception {

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

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/hotels")
                        .content(jsonContentNewHotel)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        localizationRepository.delete(newLocalization);
    }

    @Test
    @WithMockUser(roles = {"USER"})
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
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/hotels/Rooms/" + newHotel.getId()))
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
        Hotel[] hotelsArray = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Hotel[].class);
        List<Hotel> hotels = new ArrayList<>((Arrays.asList(hotelsArray)));

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
    @WithMockUser(roles = {"ADMIN"})
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
    @WithMockUser(roles = {"USER"})
    public void shouldEditHotelUser() throws Exception {

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
    public void shouldDeleteHotelUser() throws Exception {

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

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/hotels/" + newHotel.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        hotelRepository.delete(newHotel);
        localizationRepository.delete(newLocalization);
    }
}