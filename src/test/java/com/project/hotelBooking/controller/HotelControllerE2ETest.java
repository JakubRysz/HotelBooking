package com.project.hotelBooking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.common.CommonDatabaseUtils;
import com.project.hotelBooking.controller.mapper.HotelMapper;
import com.project.hotelBooking.controller.model.HotelDto;
import com.project.hotelBooking.repository.HotelRepository;
import com.project.hotelBooking.repository.LocalizationRepository;
import com.project.hotelBooking.repository.model.Hotel;
import com.project.hotelBooking.repository.model.Localization;
import com.project.hotelBooking.service.mapper.HotelMapperServ;
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

import static com.project.hotelBooking.common.CommonDatabaseProvider.LOCALIZATION_1;
import static com.project.hotelBooking.common.CommonDatabaseProvider.getHotel1;
import static com.project.hotelBooking.controller.CommonControllerTestConstants.ACCESS_DENIED_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
    public class HotelControllerE2ETest {

    public static final String HOTELS_URL = "/v1/hotels";
    public static final String HOTELS_ROOMS_URL = HOTELS_URL + "/rooms";
    private final ObjectMapper objectMapper;
    private final LocalizationRepository localizationRepository;
    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
    private final HotelMapperServ hotelMapperServ;
    private final MockMvc mockMvc;
    private final CommonDatabaseUtils commonDatabaseUtils;

    private Localization localization1;

    @BeforeEach
    public void initialize() {
        localization1 = localizationRepository.save(LOCALIZATION_1);
    }

    @AfterEach
    public void cleanUp(){
        commonDatabaseUtils.clearDatabaseTables();
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldCreateHotel() throws Exception {
        //given
        Hotel hotel1 = getHotel1(localization1.getId());
        HotelDto hotel1Dto = mapHotelToHotelDto(hotel1);
        final String jsonContentNewHotel = objectMapper.writeValueAsString(hotel1Dto);
        int hotelsNumberBefore = hotelRepository.findAllHotels(Pageable.unpaged()).size();

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(HOTELS_URL)
                        .content(jsonContentNewHotel)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        HotelDto hotel = getHotelFromResponse(mvcResult);
        int hotelsNumberAfter = hotelRepository.findAllHotels(Pageable.unpaged()).size();
        Hotel hotelFromDatabase = hotelRepository.findById(hotel.getId()).orElseThrow();
        assertEqualsHotelsWithoutId(hotel1, hotelFromDatabase);
        assertEquals(hotelsNumberBefore + 1, hotelsNumberAfter);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403CreateHotel_user() throws Exception {
        //given
        Hotel hotel1 = getHotel1(localization1.getId());
        HotelDto hotel1Dto = mapHotelToHotelDto(hotel1);
        final String jsonContentNewHotel = objectMapper.writeValueAsString(hotel1Dto);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(HOTELS_URL)
                        .content(jsonContentNewHotel)
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
    @WithMockUser(roles = {"USER"})
    public void shouldGetSingleHotel() throws Exception {
        //given
        Hotel hotel1 = getHotel1(localization1.getId());
        Hotel hotel1Saved = hotelRepository.save(hotel1);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(HOTELS_ROOMS_URL + "/" + hotel1Saved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        HotelDto hotel = getHotelFromResponse(mvcResult);
        assertEqualsHotels(hotel1Saved, mapHotelDtoToHotel(hotel));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldGetMultipleHotels() throws Exception {
        //given
        Hotel hotel2 = Hotel.builder()
                .name("Hilton2")
                .numberOfStars(4)
                .hotelChain("Hilton")
                .localizationId(localization1.getId())
                .build();

        Hotel hotel3 = Hotel.builder()
                .name("Hilton3")
                .numberOfStars(5)
                .hotelChain("Hilton")
                .localizationId(localization1.getId())
                .build();

        Hotel hotel1Saved = hotelRepository.save(getHotel1(localization1.getId()));
        Hotel hotel2Saved = hotelRepository.save(hotel2);
        Hotel hotel3Saved = hotelRepository.save(hotel3);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(HOTELS_ROOMS_URL))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        HotelDto[] hotelsArray = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), HotelDto[].class);
        List<HotelDto> hotels = new ArrayList<>((Arrays.asList(hotelsArray)));

        assertEquals(3, hotels.size());
        assertEqualsHotels(hotel1Saved, mapHotelDtoToHotel(hotels.get(0)));
        assertEqualsHotels(hotel2Saved, mapHotelDtoToHotel(hotels.get(1)));
        assertEqualsHotels(hotel3Saved, mapHotelDtoToHotel(hotels.get(2)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldEditHotel() throws Exception {
        //given
        Hotel hotel1 = getHotel1(localization1.getId());
        Hotel hotel1Saved = hotelRepository.save(hotel1);

        HotelDto hotelEdited = HotelDto.builder()
                .id(hotel1Saved.getId())
                .name("HotelNamedEdited")
                .numberOfStars(hotel1Saved.getNumberOfStars())
                .hotelChain(hotel1Saved.getHotelChain())
                .localizationId(hotel1Saved.getLocalizationId())
                .build();

        final String jsonContentHotelEdited = objectMapper.writeValueAsString(hotelEdited);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(HOTELS_URL)
                        .content(jsonContentHotelEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        HotelDto hotel = getHotelFromResponse(mvcResult);
        Hotel hotelFromDatabase = hotelRepository.findById(hotel.getId()).orElseThrow();
        assertEqualsHotelsWithoutId(mapHotelDtoToHotel(hotelEdited), hotelFromDatabase);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403EditHotelUser() throws Exception {
        //given
        Hotel hotel1 = getHotel1(localization1.getId());
        Hotel hotel1Saved = hotelRepository.save(hotel1);

        HotelDto hotelEdited = HotelDto.builder()
                .id(hotel1Saved.getId())
                .name("HotelNamedEdited")
                .numberOfStars(hotel1Saved.getNumberOfStars())
                .hotelChain(hotel1Saved.getHotelChain())
                .localizationId(hotel1Saved.getLocalizationId())
                .build();

        final String jsonContentHotelEdited = objectMapper.writeValueAsString(hotelEdited);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(HOTELS_URL)
                        .content(jsonContentHotelEdited)
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
    public void shouldDeleteHotel() throws Exception {
        //given
        Hotel hotel1 = getHotel1(localization1.getId());
        Hotel hotel1Saved = hotelRepository.save(hotel1);
        int hotelsNumberBefore = hotelRepository.findAllHotels(Pageable.unpaged()).size();

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete(HOTELS_URL + "/" +hotel1Saved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        int hotelsNumberAfter = hotelRepository.findAllHotels(Pageable.unpaged()).size();

        //then
        assertEquals(hotelsNumberBefore - 1, hotelsNumberAfter);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403DeleteHotelUser() throws Exception {
        //given
        Hotel hotel1 = getHotel1(localization1.getId());
        Hotel hotel1Saved = hotelRepository.save(hotel1);

        //when & then
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(HOTELS_URL + "/" + hotel1Saved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andReturn();

        // then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(ACCESS_DENIED_MESSAGE, responseMessage);
    }

    private HotelDto getHotelFromResponse(MvcResult mvcResult) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), HotelDto.class);
    }

    private static void assertEqualsHotelsWithoutId(Hotel expectedHotel, Hotel actualHotel) {
        assertEquals(expectedHotel.getName(), actualHotel.getName());
        assertEquals(expectedHotel.getNumberOfStars(), actualHotel.getNumberOfStars());
        assertEquals(expectedHotel.getHotelChain(), actualHotel.getHotelChain());
        assertEquals(expectedHotel.getLocalizationId(), actualHotel.getLocalizationId());
    }

    private static void assertEqualsHotels(Hotel expectedHotel, Hotel actualHotel) {
        assertEquals(expectedHotel.getId(), actualHotel.getId());
        assertEqualsHotelsWithoutId(expectedHotel, actualHotel);
    }

    private HotelDto mapHotelToHotelDto(Hotel hotel) {
        return hotelMapper.mapToHotelDto(hotelMapperServ.mapToHotel(hotel));
    }

    private Hotel mapHotelDtoToHotel(HotelDto hotelDto) {
        return hotelMapperServ.mapToRepositoryHotel(hotelMapper.mapToHotel(hotelDto));
    }
}