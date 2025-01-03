package com.project.hotelBooking.security.controller;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.common.CommonDatabaseUtils;
import com.project.hotelBooking.repository.UserRepository;
import com.project.hotelBooking.repository.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.stream.Stream;

import static com.project.hotelBooking.common.CommonTestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LoginControllerE2ETest {

    public static final String LOGIN_URL = "/login";
    public static final String AUTHENTICATION_ERROR_INVALID_USERNAME_OR_PASSWORD = "Authentication error: Invalid username or password";
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    private final PasswordEncoder passwordEncoder;
    private final CommonDatabaseUtils commonDatabaseUtils;

    private static final String username1 = "username1";
    private static final String password1 = "password123";

    private static User user1 = User.builder()
            .firstName("Paul")
            .lastName("Smith")
            .dateOfBirth(LocalDate.now().minusYears(25))
            .username(username1)
            .email(EMAIL_TEST)
            .build();

    @AfterEach
    public void cleanUp(){
        commonDatabaseUtils.clearDatabaseTables();
    }

    private static Stream<Arguments> provideRole() {
        return Stream.of(
                Arguments.of(ROLE_ADMIN, true),
                Arguments.of(ROLE_USER, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideRole")
    public void shouldLogUser_whenValidCredentials(String role, boolean isAdmin) throws Exception {
        // given
        user1.setPassword(passwordEncoder.encode(password1));
        user1.setRole(role);
        userRepository.save(user1);
        LoginCredentials loginCredentials = new LoginCredentials(username1, password1);
        String jsonLoginCredentials = objectMapper.writeValueAsString(loginCredentials);
        // when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                        .content(jsonLoginCredentials)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();
        // then
        Token token = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Token.class);
        assertEquals(username1, JWT.decode(token.getToken()).getSubject());
        assertEquals(token.adminAccess, isAdmin);
    }

    @Test
    public void shouldReturn401HttpStatus_whenUserExists_butInvalidPasswordProvided() throws Exception {
        // given
        user1.setPassword(passwordEncoder.encode(password1));
        user1.setRole(ROLE_USER);
        userRepository.save(user1);
        LoginCredentials loginCredentials = new LoginCredentials(username1, "bad_password");
        String jsonLoginCredentials = objectMapper.writeValueAsString(loginCredentials);

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                        .content(jsonLoginCredentials)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(401))
                .andReturn();
        // then
        String response = result.getResponse().getContentAsString();
        assertEquals(AUTHENTICATION_ERROR_INVALID_USERNAME_OR_PASSWORD, response);
    }

    @Test
    public void shouldReturn401HttpStatus_whenUserNotExists() throws Exception {
        // given
        LoginCredentials loginCredentials = new LoginCredentials("not_existing_user", "bad_password");
        String jsonLoginCredentials = objectMapper.writeValueAsString(loginCredentials);

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                        .content(jsonLoginCredentials)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(401))
                .andReturn();

        // then
        String response = result.getResponse().getContentAsString();
        assertEquals(AUTHENTICATION_ERROR_INVALID_USERNAME_OR_PASSWORD, response);
    }

    @AllArgsConstructor
    private static class LoginCredentials {
        @JsonProperty("username")
        private String username;
        @JsonProperty("password")
        private String password;
    }

    @Getter
    @AllArgsConstructor
    private static class Token {
        private String token;
        private boolean adminAccess;
    }
}
