package com.project.hotelBooking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.common.CommonDatabaseUtils;
import com.project.hotelBooking.controller.mapper.HotelMapper;
import com.project.hotelBooking.controller.mapper.LocalizationMapper;
import com.project.hotelBooking.controller.model.HotelDto;
import com.project.hotelBooking.controller.model.LocalizationDto;
import com.project.hotelBooking.controller.model.LocalizationWithHotelsDto;
import com.project.hotelBooking.repository.HotelRepository;
import com.project.hotelBooking.repository.LocalizationRepository;
import com.project.hotelBooking.repository.model.Hotel;
import com.project.hotelBooking.repository.model.Localization;
import com.project.hotelBooking.service.mapper.HotelMapperServ;
import com.project.hotelBooking.service.mapper.LocalizationMapperServ;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.project.hotelBooking.common.CommonTestConstants.*;

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
class LocalizationControllerE2ETest {

    private static final String LOCALIZATIONS_URL = "/v1/localizations/";
    private static final String LOCALIZATIONS_WITH_HOTELS_URL = "/v1/localizations/hotels/";
    private final ObjectMapper objectMapper;
    private final LocalizationRepository localizationRepository;
    private final HotelRepository hotelRepository;
    private final CommonDatabaseUtils commonDatabaseUtils;
    private final MockMvc mockMvc;
    private final LocalizationMapper localizationMapper;
    private final LocalizationMapperServ localizationMapperServ;
    private final HotelMapper hotelMapper;
    private final HotelMapperServ hotelMapperServ;

    @AfterEach
    public void clear() {
        commonDatabaseUtils.clearDatabaseTables();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldCreateLocalization() throws Exception {
        //given
        LocalizationDto localization1Dto  = localizationMapper.mapToLocalizationDto(
                localizationMapperServ.mapToLocalization(LOCALIZATION_1));
        final String jsonContentNewLocalization = objectMapper.writeValueAsString(localization1Dto);
        int localizationsNumberBefore = localizationRepository.findAllLocalizations(Pageable.unpaged()).size();

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(LOCALIZATIONS_URL)
                        .content(jsonContentNewLocalization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        LocalizationDto localization = getLocalizationFromResponse(mvcResult.getResponse());
        int localizationsNumberAfter = localizationRepository.findAllLocalizations(Pageable.unpaged()).size();
        Localization localizationFromDatabase = localizationRepository.findById(localization.getId()).orElseThrow();
        assertEqualsLocalizationsWithoutId(LOCALIZATION_1, localizationFromDatabase);
        assertEquals(localizationsNumberBefore + 1, localizationsNumberAfter);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403CreateLocalizationUser() throws Exception {
        //given
        LocalizationDto localization1Dto  = localizationMapper.mapToLocalizationDto(
                localizationMapperServ.mapToLocalization(LOCALIZATION_1));
        final String jsonContentNewLocalization = objectMapper.writeValueAsString(localization1Dto);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post(LOCALIZATIONS_URL)
                        .content(jsonContentNewLocalization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldGetSingleLocalizationWithHotels() throws Exception {
        //given
        Localization localizationSaved = localizationRepository.save(LOCALIZATION_1);

        Hotel hotel1 = Hotel.builder()
                .name("Hilton1")
                .numberOfStars(3)
                .hotelChain(HILTON_CHAIN)
                .localizationId(localizationSaved.getId())
                .build();

        hotelRepository.save(hotel1);
        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(LOCALIZATIONS_WITH_HOTELS_URL + localizationSaved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        LocalizationWithHotelsDto localizationRetrieved = getLocalizationWithHotelsFromResponse(mvcResult.getResponse());
        assertEqualsLocalizationsWithoutHotels(localizationSaved, localizationRetrieved);
        assertEquals(List.of(mapHotelToHotelDto(hotel1)), localizationRetrieved.getHotels());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldGetMultipleLocalizations() throws Exception {
        //given
        Localization newLocalization2 = Localization.builder()
                .city(WARSAW_CITY)
                .country(POLAND_COUNTRY)
                .build();

        Localization localizationSaved1 = localizationRepository.save(LOCALIZATION_1);
        Localization localizationSaved2 = localizationRepository.save(newLocalization2);

        Hotel hotel1 = Hotel.builder()
                .name("Hilton1")
                .numberOfStars(3)
                .hotelChain(HILTON_CHAIN)
                .localizationId(localizationSaved1.getId())
                .build();

        Hotel hotel2 = Hotel.builder()
                .name("Hilton2")
                .numberOfStars(3)
                .hotelChain(HILTON_CHAIN)
                .localizationId(localizationSaved1.getId())
                .build();

        Hotel hotel3 = Hotel.builder()
                .name("Hilton3")
                .numberOfStars(3)
                .hotelChain(HILTON_CHAIN)
                .localizationId(localizationSaved2.getId())
                .build();

        hotelRepository.save(hotel1);
        hotelRepository.save(hotel2);
        hotelRepository.save(hotel3);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(LOCALIZATIONS_WITH_HOTELS_URL))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        LocalizationWithHotelsDto[] localizationsArray = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), LocalizationWithHotelsDto[].class);
        List<LocalizationWithHotelsDto> localizations = new ArrayList<>((Arrays.asList(localizationsArray)));

        assertEquals(2, localizations.size());
        assertEqualsLocalizationsWithoutHotels(localizationSaved1, localizations.get(0));
        assertEqualsLocalizationsWithoutHotels(localizationSaved2, localizations.get(1));
        assertEquals(List.of(mapHotelToHotelDto(hotel1), mapHotelToHotelDto(hotel2)), localizations.get(0).getHotels());
        assertEquals(List.of(mapHotelToHotelDto(hotel3)), localizations.get(1).getHotels());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldEditLocalization() throws Exception {
        //given
        Localization localizationSaved = localizationRepository.save(LOCALIZATION_1);

        LocalizationDto localizationEdited = LocalizationDto.builder()
                .id(localizationSaved.getId())
                .city(GDANSK_CITY)
                .country(POLAND_COUNTRY)
                .build();
        final String jsonContentLocalizationEdited = objectMapper.writeValueAsString(localizationEdited);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(LOCALIZATIONS_URL)
                        .content(jsonContentLocalizationEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        Localization localizationGet = localizationRepository.findById(localizationEdited.getId()).orElseThrow();

        //then
        LocalizationDto localization = getLocalizationFromResponse(mvcResult.getResponse());
        assertEquals(localizationEdited.getId(), localization.getId());
        assertEquals(localizationEdited.getCity(), localizationGet.getCity());
        assertEquals(localizationEdited.getCountry(), localizationGet.getCountry());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403EditLocalizationUser() throws Exception {
        //given
        Localization localizationSaved = localizationRepository.save(LOCALIZATION_1);

        Localization localizationEdited = new Localization();
        localizationEdited.setId(localizationSaved.getId());
        localizationEdited.setCity(GDANSK_CITY);
        localizationEdited.setCountry(POLAND_COUNTRY);
        final String jsonContentLocalizationEdited = objectMapper.writeValueAsString(localizationEdited);

        //when & them
        mockMvc.perform(MockMvcRequestBuilders.put(LOCALIZATIONS_URL)
                        .content(jsonContentLocalizationEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldDeleteLocalization() throws Exception {
        //given
        Localization localizationSaved = localizationRepository.save(LOCALIZATION_1);
        int localizationsNumberBefore = localizationRepository.findAllLocalizations(Pageable.unpaged()).size();

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete(LOCALIZATIONS_URL + localizationSaved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        int localizationsNumberAfter = localizationRepository.findAllLocalizations(Pageable.unpaged()).size();

        //then
        assertEquals(localizationsNumberBefore - 1, localizationsNumberAfter);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403DeleteLocalizationUser() throws Exception {
        //given
        Localization localizationSaved = localizationRepository.save(LOCALIZATION_1);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.delete(LOCALIZATIONS_URL + localizationSaved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

    private LocalizationDto getLocalizationFromResponse(MockHttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(response.getContentAsString(), LocalizationDto.class);
    }

    private LocalizationWithHotelsDto getLocalizationWithHotelsFromResponse(MockHttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(response.getContentAsString(), LocalizationWithHotelsDto.class);
    }

    private static void assertEqualsLocalizationsWithoutId(Localization expectedlocalization, Localization actualLocalization) {
        assertEquals(expectedlocalization.getCity(), actualLocalization.getCity());
        assertEquals(expectedlocalization.getCountry(), actualLocalization.getCountry());
    }

    private static void assertEqualsLocalizationsWithoutHotels(Localization expectedlocalization, LocalizationWithHotelsDto actualLocalization) {
        assertEquals(expectedlocalization.getId(), actualLocalization.getId());
        assertEquals(expectedlocalization.getCity(), actualLocalization.getCity());
        assertEquals(expectedlocalization.getCountry(), actualLocalization.getCountry());
    }

    private HotelDto mapHotelToHotelDto(Hotel hotel1Saved1) {
        return hotelMapper.mapToHotelDto(hotelMapperServ.mapToHotel(hotel1Saved1));
    }
}