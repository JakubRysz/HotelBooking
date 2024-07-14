package com.project.hotelBooking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.common.CommonDatabaseUtils;
import com.project.hotelBooking.controller.mapper.UserMapper;
import com.project.hotelBooking.controller.model.UserDto;
import com.project.hotelBooking.repository.UserRepository;
import com.project.hotelBooking.repository.model.User;
import com.project.hotelBooking.service.SimpleEmailService;
import com.project.hotelBooking.service.mapper.UserMapperServ;
import com.project.hotelBooking.service.model.Mail;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import static com.project.hotelBooking.common.CommonDatabaseProvider.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserControllerE2ETest {

    private static final String USERS_URL = "/v1/users/";
    private static final String USERS_BOOKINGS_URL = USERS_URL + "bookings/";
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final MockMvc mockMvc;
    private final UserMapper userMapper;
    private final UserMapperServ userMapperServ;
    private final CommonDatabaseUtils commonDatabaseUtils;
    @MockBean
    private SimpleEmailService emailService;

    @BeforeEach
    public void initialize() {
        doNothing().when(emailService).send(any(Mail.class));
    }

    @AfterEach
    public void clear() {
        commonDatabaseUtils.clearDatabaseTables();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldCreateUser() throws Exception {
        //given
        UserDto user1Dto = mapUserToUserDto(USER_1);
        final String jsonContentNewUser = objectMapper.writeValueAsString(user1Dto);
        int usersNumberBefore = userRepository.findAllUsers(Pageable.unpaged()).size();

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(USERS_URL)
                        .content(jsonContentNewUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        UserDto user = getUserFromResponse(mvcResult.getResponse());
        int usersNumberAfter = userRepository.findAllUsers(Pageable.unpaged()).size();
        User userFromDatabase = userRepository.findById(user.getId()).orElseThrow();
        assertEqualsUsersWithoutId(USER_1, userFromDatabase);
        assertEquals(usersNumberBefore + 1, usersNumberAfter);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403CreateUserUser() throws Exception {
        //given
        UserDto user1Dto = mapUserToUserDto(USER_1);
        final String jsonContentNewUser = objectMapper.writeValueAsString(user1Dto);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post(USERS_URL)
                        .content(jsonContentNewUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldRegisterUser() throws Exception {
        //given
        UserDto user1Dto = mapUserToUserDto(USER_1);
        final String jsonContentNewUser = objectMapper.writeValueAsString(user1Dto);
        int usersNumberBefore = userRepository.findAllUsers(Pageable.unpaged()).size();

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(USERS_URL + "registration")
                        .content(jsonContentNewUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        UserDto user = getUserFromResponse(mvcResult.getResponse());
        int usersNumberAfter = userRepository.findAllUsers(Pageable.unpaged()).size();
        User userFromDatabase = userRepository.findById(user.getId()).orElseThrow();
        assertEqualsUsersWithoutId(USER_1, userFromDatabase);
        assertEquals(usersNumberBefore + 1, usersNumberAfter);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldGetSingleUser() throws Exception {
        //given
        User userSaved = userRepository.save(USER_1);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(USERS_BOOKINGS_URL + userSaved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        UserDto user = getUserFromResponse(mvcResult.getResponse());
        User userFromDatabase = userRepository.findById(user.getId()).orElseThrow();
        assertEqualsUsersWithoutBookings(userSaved, userFromDatabase);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403GetSingleUserUser() throws Exception {
        //given
        User userSaved = userRepository.save(USER_1);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get(USERS_BOOKINGS_URL + userSaved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldGetMultipleUsers() throws Exception {
        //given
        User userSaved1 = userRepository.save(USER_1);
        User userSaved2 = userRepository.save(USER_2);
        User userSaved3 = userRepository.save(USER_3);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(USERS_BOOKINGS_URL))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        UserDto[] usersArray = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto[].class);
        List<UserDto> users = new ArrayList<>((Arrays.asList(usersArray)));

        assertEquals(3, users.size());
        assertEqualsUsersWithoutBookings(userSaved1, mapUserToUser(users.get(0)));
        assertEqualsUsersWithoutBookings(userSaved2, mapUserToUser(users.get(1)));
        assertEqualsUsersWithoutBookings(userSaved3, mapUserToUser(users.get(2)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403GetMultipleUsersUser() throws Exception {
        //given
        userRepository.save(USER_1);
        userRepository.save(USER_2);
        userRepository.save(USER_3);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get(USERS_BOOKINGS_URL))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldEditUser() throws Exception {
        //given
        User userSaved = userRepository.save(USER_1);
        String usernameEdited = "usernameEdited";
        UserDto userEdited = UserDto.builder()
                .id(userSaved.getId())
                .firstName(USER_1.getFirstName())
                .lastName(USER_1.getLastName())
                .dateOfBirth(USER_1.getDateOfBirth())
                .username(usernameEdited)
                .password(USER_1.getPassword())
                .role(USER_1.getRole())
                .email(USER_1.getEmail())
                .build();
        final String jsonContentUserEdited = objectMapper.writeValueAsString(userEdited);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(USERS_URL)
                        .content(jsonContentUserEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        UserDto user = getUserFromResponse(mvcResult.getResponse());
        User userFromDatabase = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(userSaved.getId(), userFromDatabase.getId());
        assertEquals(userSaved.getFirstName(), userFromDatabase.getFirstName());
        assertEquals(userSaved.getLastName(), userFromDatabase.getLastName());
        assertEquals(userSaved.getDateOfBirth(), userFromDatabase.getDateOfBirth());
        assertEquals(usernameEdited, userFromDatabase.getUsername());
        assertEquals(userSaved.getRole(), userFromDatabase.getRole());
        assertEquals(userSaved.getEmail(), userFromDatabase.getEmail());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403EditUserUser() throws Exception {
        //given
        User userSaved = userRepository.save(USER_1);
        UserDto userEdited = UserDto.builder()
                .id(userSaved.getId())
                .firstName(USER_1.getFirstName())
                .lastName(USER_1.getLastName())
                .dateOfBirth(USER_1.getDateOfBirth())
                .username("usernameEdited")
                .password(USER_1.getPassword())
                .role(USER_1.getRole())
                .email(USER_1.getEmail())
                .build();
        final String jsonContentUserEdited = objectMapper.writeValueAsString(userEdited);
        //when
        mockMvc.perform(MockMvcRequestBuilders.put(USERS_URL)
                        .content(jsonContentUserEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldDeleteUser() throws Exception {
        //given
        User userSaved = userRepository.save(USER_1);
        int usersNumberBefore = userRepository.findAllUsers(Pageable.unpaged()).size();
        //when
        mockMvc.perform(MockMvcRequestBuilders.delete(USERS_URL + userSaved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        int usersNumberAfter = userRepository.findAllUsers(Pageable.unpaged()).size();

        //then
        assertEquals(usersNumberBefore - 1, usersNumberAfter);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403DeleteUserUser() throws Exception {
        //given
        userRepository.save(USER_1);
        //when & then
        mockMvc.perform(MockMvcRequestBuilders.delete(USERS_URL + USER_1.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));
    }


    private static void assertEqualsUsersWithoutId(User expectedUser, User actualUser) {
        assertEquals(expectedUser.getFirstName(), actualUser.getFirstName());
        assertEquals(expectedUser.getLastName(), actualUser.getLastName());
        assertEquals(expectedUser.getDateOfBirth(), actualUser.getDateOfBirth());
        assertEquals(expectedUser.getUsername(), actualUser.getUsername());
        assertEquals(expectedUser.getRole(), actualUser.getRole());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
    }

    private static void assertEqualsUsersWithoutBookings(User expectedUser, User actualUser) {
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getFirstName(), actualUser.getFirstName());
        assertEquals(expectedUser.getLastName(), actualUser.getLastName());
        assertEquals(expectedUser.getDateOfBirth(), actualUser.getDateOfBirth());
        assertEquals(expectedUser.getUsername(), actualUser.getUsername());
        assertEquals(expectedUser.getRole(), actualUser.getRole());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
    }

    private UserDto getUserFromResponse(MockHttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(response.getContentAsString(), UserDto.class);
    }

    private UserDto mapUserToUserDto(User user) {
        return userMapper.mapToUserDto(userMapperServ.mapToUser(user));
    }

    private User mapUserToUser(UserDto userDto) {
        return userMapperServ.mapToUserRepository(userMapper.mapToUser(userDto));
    }
}
