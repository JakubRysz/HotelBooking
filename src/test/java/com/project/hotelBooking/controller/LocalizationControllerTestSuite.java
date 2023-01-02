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
public class LocalizationControllerTestSuite {

    private final ObjectMapper objectMapper;
    private final LocalizationRepository localizationRepository;
    private final MockMvc mockMvc;

    @Test
    @WithMockUser(roles = {"ADMIN"})
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
        assertEquals(localizationsNumberBefore + 1, localizationsNumberAfter);

        localizationRepository.delete(newLocalization);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldCreateLocalizationUser() throws Exception {

        //given
        Localization newLocalization = new Localization();
        newLocalization.setCity("Krakow");
        newLocalization.setCountry("Poland");
        final String jsonContentNewLocalization = objectMapper.writeValueAsString(newLocalization);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/localizations")
                        .content(jsonContentNewLocalization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldGetSingleLocalization() throws Exception {

        //given
        Localization newLocalization = new Localization();
        newLocalization.setCity("Krakow");
        newLocalization.setCountry("Poland");
        localizationRepository.save(newLocalization);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/localizations/Hotels/" + newLocalization.getId()))
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
    @WithMockUser(roles = {"USER"})
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
        Localization[] localizationsArray = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Localization[].class);
        List<Localization> localizations = new ArrayList<>((Arrays.asList(localizationsArray)));

        assertEquals(3, localizations.size());
        assertEquals(newLocalization3.getId(), localizations.get(2).getId());
        assertEquals(newLocalization3.getCity(), localizations.get(2).getCity());
        assertEquals(newLocalization3.getCountry(), localizations.get(2).getCountry());

        localizationRepository.delete(newLocalization1);
        localizationRepository.delete(newLocalization2);
        localizationRepository.delete(newLocalization3);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
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
    @WithMockUser(roles = {"USER"})
    public void shouldEditLocalizationUser() throws Exception {

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

        //when & them
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/localizations")
                        .content(jsonContentLocalizationEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        localizationRepository.delete(newLocalization);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldDeleteLocalization() throws Exception {

        //given
        Localization newLocalization = new Localization();
        newLocalization.setCity("Krakow");
        newLocalization.setCountry("Poland");
        localizationRepository.save(newLocalization);
        int localizationsNumberBefore = localizationRepository.findAllLocalizations(Pageable.unpaged()).size();

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/localizations/" + newLocalization.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        int localizationsNumberAfter = localizationRepository.findAllLocalizations(Pageable.unpaged()).size();

        //then
        assertEquals(localizationsNumberBefore - 1, localizationsNumberAfter);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldDeleteLocalizationUser() throws Exception {

        //given
        Localization newLocalization = new Localization();
        newLocalization.setCity("Krakow");
        newLocalization.setCountry("Poland");
        localizationRepository.save(newLocalization);
        int localizationsNumberBefore = localizationRepository.findAllLocalizations(Pageable.unpaged()).size();

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/localizations/" + newLocalization.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        localizationRepository.delete(newLocalization);
    }
}