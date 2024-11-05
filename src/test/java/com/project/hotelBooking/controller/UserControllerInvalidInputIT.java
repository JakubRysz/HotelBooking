package com.project.hotelBooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.controller.mapper.UserMapper;
import com.project.hotelBooking.controller.model.UserCreateDto;
import com.project.hotelBooking.security.SecurityConfig;
import com.project.hotelBooking.service.SimpleEmailService;
import com.project.hotelBooking.service.UserService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Stream;

import static com.project.hotelBooking.common.CommonTestConstants.EMAIL_TEST;
import static com.project.hotelBooking.common.CommonTestConstants.ROLE_USER;
import static com.project.hotelBooking.controller.UserControllerE2ETest.USERS_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(UserController.class)
@ImportAutoConfiguration(classes = SecurityConfig.class)
public class UserControllerInvalidInputIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private UserMapper userMapper;
    @MockBean
    private ValidatorCustom validatorCustom;
    @MockBean
    private SimpleEmailService emailService;
    @MockBean
    private UserDetailsService userDetailsService;

    @ParameterizedTest
    @MethodSource("incorrectUserProvider")
    @WithMockUser(roles = {"ADMIN"})
    public void shouldCreateUser(UserCreateDto userCreateDto, Map<String, String> expectedMessageMap) throws Exception {
        //given
        final String jsonContentNewUser = objectMapper.writeValueAsString(userCreateDto);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(USERS_URL)
                        .content(jsonContentNewUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andReturn();

        //then
        var actualMessageMap = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Map.class);
        assertEquals(expectedMessageMap, actualMessageMap);
    }

    static Stream<Arguments> incorrectUserProvider() {

        UserCreateDto userInvalidPassword = UserCreateDto.builder()
                .firstName("Paul")
                .lastName("Smith")
                .dateOfBirth(LocalDate.now().minusYears(22))
                .username("paulsmith")
                .password("aaa")
                .confirmPassword("aaa")
                .role(ROLE_USER)
                .email(EMAIL_TEST)
                .build();

        UserCreateDto userPasswordsDoNotMatch = UserCreateDto.builder()
                .firstName("Paul")
                .lastName("Smith")
                .dateOfBirth(LocalDate.now().minusYears(22))
                .username("paulsmith")
                .password("Paulsmith123!")
                .confirmPassword("Paaulsmith123!")
                .role(ROLE_USER)
                .email(EMAIL_TEST)
                .build();

        UserCreateDto userPasswordsDoNotMatchOneEmpty = UserCreateDto.builder()
                .firstName("Paul")
                .lastName("Smith")
                .dateOfBirth(LocalDate.now().minusYears(22))
                .username("paulsmith")
                .password("Paulsmith123!")
                .confirmPassword("")
                .role(ROLE_USER)
                .email(EMAIL_TEST)
                .build();

        UserCreateDto userEmptyPassword = UserCreateDto.builder()
                .firstName("Paul")
                .lastName("Smith")
                .dateOfBirth(LocalDate.now().minusYears(22))
                .username("paulsmith")
                .password("")
                .confirmPassword("")
                .role(ROLE_USER)
                .email(EMAIL_TEST)
                .build();

        UserCreateDto userNullPassword = UserCreateDto.builder()
                .firstName("Paul")
                .lastName("Smith")
                .dateOfBirth(LocalDate.now().minusYears(22))
                .username("paulsmith")
                .role(ROLE_USER)
                .email(EMAIL_TEST)
                .build();

        return Stream.of(
                Arguments.of(userInvalidPassword,
                        Map.of("password", "password must be 6 or more characters in length, password must contain 1 or more uppercase characters, password must contain 1 or more digit characters, password must contain 1 or more special characters",
                                "confirmPassword", "password must be 6 or more characters in length, password must contain 1 or more uppercase characters, password must contain 1 or more digit characters, password must contain 1 or more special characters")
                ),
                Arguments.of(userPasswordsDoNotMatch,
                        Map.of("passwordAndConfirmPasswordMatching", "passwords do not match")
                ),
                Arguments.of(userPasswordsDoNotMatchOneEmpty,
                        Map.of("passwordAndConfirmPasswordMatching", "passwords do not match",
                        "confirmPassword", "password must not be empty")
                ),
                Arguments.of(userEmptyPassword,
                        Map.of("password", "password must not be empty",
                                "confirmPassword", "password must not be empty")
                ),
                Arguments.of(userNullPassword,
                        Map.of("password", "password must not be null",
                                "confirmPassword", "password must not be null")
                )
        );
    }
}
