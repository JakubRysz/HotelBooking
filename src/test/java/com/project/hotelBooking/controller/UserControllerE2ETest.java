package com.project.hotelBooking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.common.CommonDatabaseUtils;
import com.project.hotelBooking.controller.mapper.UserMapper;
import com.project.hotelBooking.controller.model.UserCreateAdminDto;
import com.project.hotelBooking.controller.model.UserCreateDto;
import com.project.hotelBooking.controller.model.UserDto;
import com.project.hotelBooking.repository.UserRepository;
import com.project.hotelBooking.repository.model.User;
import com.project.hotelBooking.service.SimpleEmailService;
import com.project.hotelBooking.service.mapper.UserMapperServ;
import com.project.hotelBooking.service.model.Mail;
import com.project.hotelBooking.service.model.UserServ;
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
import static com.project.hotelBooking.controller.CommonControllerTestConstants.ACCESS_DENIED_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserControllerE2ETest {

    protected static final String USERS_URL = "/v1/users";
    protected static final String USERS_REGISTRATION = USERS_URL + "/registration";
    private static final String USERS_BOOKINGS_URL = USERS_URL + "/bookings";
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
    public void cleanUp(){
        commonDatabaseUtils.clearDatabaseTables();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldCreateUser() throws Exception {
        //given
        UserCreateAdminDto user1Dto = mapUserToUserCreateDtoAdmin(USER_1, USER_1.getPassword());
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
        UserCreateDto user1Dto = mapUserToUserCreateDto(USER_1, USER_1.getPassword());
        final String jsonContentNewUser = objectMapper.writeValueAsString(user1Dto);

        //when & then
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(USERS_URL)
                        .content(jsonContentNewUser)
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
    public void shouldRegisterUser() throws Exception {
        //given
        UserCreateDto user1Dto = mapUserToUserCreateDto(USER_1, USER_1.getPassword());
        final String jsonContentNewUser = objectMapper.writeValueAsString(user1Dto);
        int usersNumberBefore = userRepository.findAllUsers(Pageable.unpaged()).size();

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(USERS_REGISTRATION)
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
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(USERS_BOOKINGS_URL + "/" + userSaved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        UserDto user = getUserFromResponse(mvcResult.getResponse());
        User userFromDatabase = userRepository.findById(user.getId()).orElseThrow();
        assertEqualsUsers(userSaved, userFromDatabase);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403GetSingleUserUser() throws Exception {
        //given
        User userSaved = userRepository.save(USER_1);

        //when & then
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(USERS_BOOKINGS_URL + "/" + userSaved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andReturn();

        // then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(ACCESS_DENIED_MESSAGE, responseMessage);
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
        assertEqualsUsers(userSaved1, mapUserDtoToUser(users.get(0)));
        assertEqualsUsers(userSaved2, mapUserDtoToUser(users.get(1)));
        assertEqualsUsers(userSaved3, mapUserDtoToUser(users.get(2)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403GetMultipleUsersUser() throws Exception {
        //given
        userRepository.save(USER_1);
        userRepository.save(USER_2);
        userRepository.save(USER_3);

        //when & then
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(USERS_BOOKINGS_URL))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andReturn();

        // then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(ACCESS_DENIED_MESSAGE, responseMessage);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldEditUser() throws Exception {
        //given
        User userSaved = userRepository.save(USER_1);
        UserDto userEdited = UserDto.builder()
                .id(userSaved.getId())
                .firstName(USER_1.getFirstName())
                .lastName(USER_1.getLastName())
                .dateOfBirth(USER_1.getDateOfBirth())
                .username("usernameEdited")
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
        assertEqualsUsersWithoutId(mapUserDtoToUser(userEdited), userFromDatabase);
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
                .role(USER_1.getRole())
                .email(USER_1.getEmail())
                .build();
        final String jsonContentUserEdited = objectMapper.writeValueAsString(userEdited);
        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(USERS_URL)
                        .content(jsonContentUserEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andReturn();

        // then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(ACCESS_DENIED_MESSAGE, responseMessage);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldDeleteUser() throws Exception {
        //given
        User userSaved = userRepository.save(USER_1);
        int usersNumberBefore = userRepository.findAllUsers(Pageable.unpaged()).size();
        //when
        mockMvc.perform(MockMvcRequestBuilders.delete(USERS_URL + "/" + userSaved.getId()))
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
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(USERS_URL + "/" + USER_1.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andReturn();

        // then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(ACCESS_DENIED_MESSAGE, responseMessage);
    }


    private static void assertEqualsUsersWithoutId(User expectedUser, User actualUser) {
        assertEquals(expectedUser.getFirstName(), actualUser.getFirstName());
        assertEquals(expectedUser.getLastName(), actualUser.getLastName());
        assertEquals(expectedUser.getDateOfBirth(), actualUser.getDateOfBirth());
        assertEquals(expectedUser.getUsername(), actualUser.getUsername());
        assertEquals(expectedUser.getRole(), actualUser.getRole());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
    }

    private static void assertEqualsUsers(User expectedUser, User actualUser) {
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEqualsUsersWithoutId(expectedUser, actualUser);
    }

    private UserDto getUserFromResponse(MockHttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(response.getContentAsString(), UserDto.class);
    }

    private UserDto mapUserToUserDto(User user) {
        return userMapper.mapToUserDto(userMapperServ.mapToUser(user));
    }

    private UserCreateAdminDto mapUserToUserCreateDtoAdmin(User user, String confirmPassword) {
        return mapToUserCreateDtoAdmin(userMapperServ.mapToUser(user), confirmPassword);
    }

    private UserCreateDto mapUserToUserCreateDto(User user, String confirmPassword) {
        return mapToUserCreateDto(userMapperServ.mapToUser(user), confirmPassword);
    }

    // method just for test purpose
    private static UserCreateAdminDto mapToUserCreateDtoAdmin(UserServ userServ, String confirmPassword) {
        return UserCreateAdminDto.builder()
                .firstName(userServ.getFirstName())
                .lastName(userServ.getLastName())
                .dateOfBirth(userServ.getDateOfBirth())
                .username(userServ.getUsername())
                .password(userServ.getPassword())
                .confirmPassword(confirmPassword)
                .role(userServ.getRole())
                .email(userServ.getEmail())
                .build();
    }

    // method just for test purpose
    private static UserCreateDto mapToUserCreateDto(UserServ userServ, String confirmPassword) {
        return UserCreateDto.builder()
                .firstName(userServ.getFirstName())
                .lastName(userServ.getLastName())
                .dateOfBirth(userServ.getDateOfBirth())
                .username(userServ.getUsername())
                .password(userServ.getPassword())
                .confirmPassword(confirmPassword)
                .email(userServ.getEmail())
                .build();
    }

    private User mapUserDtoToUser(UserDto userDto) {
        return userMapperServ.mapToUserRepository(userMapper.mapToUser(userDto));
    }
}
