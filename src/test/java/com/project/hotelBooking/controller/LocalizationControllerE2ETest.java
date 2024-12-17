package com.project.hotelBooking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.common.CommonDatabaseUtils;
import com.project.hotelBooking.controller.mapper.HotelMapper;
import com.project.hotelBooking.controller.mapper.LocalizationMapper;
import com.project.hotelBooking.controller.model.hotel.HotelDto;
import com.project.hotelBooking.controller.model.localization.LocalizationCreateDto;
import com.project.hotelBooking.controller.model.localization.LocalizationDto;
import com.project.hotelBooking.controller.model.localization.LocalizationWithHotelsDto;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.project.hotelBooking.common.CommonDatabaseProvider.LOCALIZATION_1;
import static com.project.hotelBooking.common.CommonDatabaseProvider.getHotel1;
import static com.project.hotelBooking.common.CommonTestConstants.*;
import static com.project.hotelBooking.controller.CommonControllerTestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class LocalizationControllerE2ETest {

    private final ObjectMapper objectMapper;
    private final LocalizationRepository localizationRepository;
    private final HotelRepository hotelRepository;
    private final CommonDatabaseUtils commonDatabaseUtils;
    private final MockMvc mockMvc;
    private final LocalizationMapper localizationMapper;
    private final LocalizationMapperServ localizationMapperServ;
    private final HotelMapper hotelMapper;
    private final HotelMapperServ hotelMapperServ;

    private static final String LOCALIZATION_COULD_NOT_BE_DELETED_MESSAGE =
            "Conflict: Localization could not be deleted as there are hotels assigned to this localization";

    @AfterEach
    public void cleanUp(){
        commonDatabaseUtils.clearDatabaseTables();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldCreateLocalization() throws Exception {
        //given
        LocalizationCreateDto localization1Dto  = localizationMapper.mapToLocalizationCreateDto(
                localizationMapperServ.mapToLocalization(LOCALIZATION_1));
        final String jsonContentNewLocalization = objectMapper.writeValueAsString(localization1Dto);
        int localizationsNumberBefore = localizationRepository.findAllLocalizations(Pageable.unpaged()).size();

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(ADMIN_LOCALIZATIONS_URL)
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
    public void shouldReturnStatus403_createLocalization_withoutAdminPermission() throws Exception {
        //given
        LocalizationCreateDto localization1Dto  = localizationMapper.mapToLocalizationCreateDto(
                localizationMapperServ.mapToLocalization(LOCALIZATION_1));
        final String jsonContentNewLocalization = objectMapper.writeValueAsString(localization1Dto);

        //when & then
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(ADMIN_LOCALIZATIONS_URL)
                        .content(jsonContentNewLocalization)
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
    public void shouldGetSingleLocalizationWithHotels_user() throws Exception {
        //given
        Localization localizationSaved = localizationRepository.save(LOCALIZATION_1);
        Hotel hotel1Saved = hotelRepository.save(getHotel1(localizationSaved.getId()));

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(LOCALIZATIONS_WITH_HOTELS_URL + "/" + localizationSaved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        LocalizationWithHotelsDto localizationRetrieved = getLocalizationWithHotelsFromResponse(mvcResult.getResponse());
        assertEqualsLocalizationsWithHotels(localizationSaved, localizationRetrieved, List.of(mapHotelToHotelDto(hotel1Saved)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldGetMultipleLocalizationsWithHotels_user() throws Exception {
        //given
        Localization newLocalization2 = Localization.builder()
                .city(WARSAW_CITY)
                .country(POLAND_COUNTRY)
                .build();

        Localization localizationSaved1 = localizationRepository.save(LOCALIZATION_1);
        Localization localizationSaved2 = localizationRepository.save(newLocalization2);

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

        Hotel hotel1Saved = hotelRepository.save(getHotel1(localizationSaved1.getId()));
        Hotel hotel2Saved = hotelRepository.save(hotel2);
        Hotel hotel3Saved = hotelRepository.save(hotel3);

        List<HotelDto> expectedHotelsLocalization1 = List.of(mapHotelToHotelDto(hotel1Saved), mapHotelToHotelDto(hotel2Saved));
        List<HotelDto> expectedHotelsLocalization2 = List.of(mapHotelToHotelDto(hotel3Saved));

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

        assertEqualsLocalizationsWithHotels(localizationSaved1, localizations.get(0), expectedHotelsLocalization1);
        assertEqualsLocalizationsWithHotels(localizationSaved2, localizations.get(1), expectedHotelsLocalization2);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldGetMultipleLocalizations_user() throws Exception {
        //given
        Localization newLocalization2 = Localization.builder()
                .city(WARSAW_CITY)
                .country(POLAND_COUNTRY)
                .build();

        Localization localizationSaved1 = localizationRepository.save(LOCALIZATION_1);
        Localization localizationSaved2 = localizationRepository.save(newLocalization2);

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

        hotelRepository.save(getHotel1(localizationSaved1.getId()));
        hotelRepository.save(hotel2);
        hotelRepository.save(hotel3);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(LOCALIZATIONS_URL))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        LocalizationDto[] localizationsArray = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), LocalizationDto[].class);
        List<LocalizationDto> localizations = new ArrayList<>((Arrays.asList(localizationsArray)));

        assertEquals(2, localizations.size());
        assertEqualsLocalizations(localizationSaved1, localizations.get(0));
        assertEqualsLocalizations(localizationSaved2, localizations.get(1));
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
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(ADMIN_LOCALIZATIONS_URL)
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
    public void shouldReturnStatus403_editLocalization_withoutAdminPermission() throws Exception {
        //given
        Localization localizationSaved = localizationRepository.save(LOCALIZATION_1);

        Localization localizationEdited = new Localization();
        localizationEdited.setId(localizationSaved.getId());
        localizationEdited.setCity(GDANSK_CITY);
        localizationEdited.setCountry(POLAND_COUNTRY);
        final String jsonContentLocalizationEdited = objectMapper.writeValueAsString(localizationEdited);

        //when & them
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(ADMIN_LOCALIZATIONS_URL)
                        .content(jsonContentLocalizationEdited)
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
    public void shouldDeleteLocalization() throws Exception {
        //given
        Localization localizationSaved = localizationRepository.save(LOCALIZATION_1);
        int localizationsNumberBefore = localizationRepository.findAllLocalizations(Pageable.unpaged()).size();

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete(ADMIN_LOCALIZATIONS_URL + "/" + localizationSaved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        int localizationsNumberAfter = localizationRepository.findAllLocalizations(Pageable.unpaged()).size();

        //then
        assertEquals(localizationsNumberBefore - 1, localizationsNumberAfter);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403_deleteLocalization_withoutAdminPermission() throws Exception {
        //given
        Localization localizationSaved = localizationRepository.save(LOCALIZATION_1);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(ADMIN_LOCALIZATIONS_URL + "/" + localizationSaved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andReturn();

        // then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(ACCESS_DENIED_MESSAGE, responseMessage);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldReturnStatus409_deleteLocalization_whenLocalizationHasHotels() throws Exception {
        //given
        Localization newLocalization2 = Localization.builder()
                .city(WARSAW_CITY)
                .country(POLAND_COUNTRY)
                .build();

        Localization localizationSaved1 = localizationRepository.save(LOCALIZATION_1);
        localizationRepository.save(newLocalization2);

        hotelRepository.save(getHotel1(localizationSaved1.getId()));



        int localizationsNumberBefore = localizationRepository.findAllLocalizations(Pageable.unpaged()).size();

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(ADMIN_LOCALIZATIONS_URL + "/" + localizationSaved1.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(409))
                .andReturn();

        int localizationsNumberAfter = localizationRepository.findAllLocalizations(Pageable.unpaged()).size();

        //then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(localizationsNumberBefore, localizationsNumberAfter);
        assertEquals(LOCALIZATION_COULD_NOT_BE_DELETED_MESSAGE, responseMessage);
    }

    private LocalizationDto getLocalizationFromResponse(MockHttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(response.getContentAsString(), LocalizationDto.class);
    }

    private LocalizationWithHotelsDto getLocalizationWithHotelsFromResponse(MockHttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(response.getContentAsString(), LocalizationWithHotelsDto.class);
    }

    private static void assertEqualsLocalizationsWithoutId(Localization expectedLocalization, Localization actualLocalization) {
        assertEquals(expectedLocalization.getCity(), actualLocalization.getCity());
        assertEquals(expectedLocalization.getCountry(), actualLocalization.getCountry());
    }

    private static void assertEqualsLocalizations(Localization expectedLocalization, LocalizationDto actualLocalization) {
        assertEquals(expectedLocalization.getCity(), actualLocalization.getCity());
        assertEquals(expectedLocalization.getCountry(), actualLocalization.getCountry());
        assertEquals(expectedLocalization.getId(), actualLocalization.getId());
    }

    private static void assertEqualsLocalizationsWithHotels(Localization expectedlocalization,
                                                            LocalizationWithHotelsDto actualLocalization,
                                                            List<HotelDto> expectedHotels) {
        assertEquals(expectedlocalization.getId(), actualLocalization.getId());
        assertEquals(expectedlocalization.getCity(), actualLocalization.getCity());
        assertEquals(expectedlocalization.getCountry(), actualLocalization.getCountry());
        assertEquals(expectedHotels, actualLocalization.getHotels());
    }

    private HotelDto mapHotelToHotelDto(Hotel hotel1Saved1) {
        return hotelMapper.mapToHotelDto(hotelMapperServ.mapToHotel(hotel1Saved1));
    }
}