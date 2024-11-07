package com.project.hotelBooking.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.security.SecurityConfig;
import com.project.hotelBooking.security.model.ChangedPassword;
import com.project.hotelBooking.security.service.LostPasswordService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;
import java.util.stream.Stream;

import static com.project.hotelBooking.security.controller.LostPasswordControllerE2ETest.CHANGE_PASSWORD_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(LostPasswordController.class)
@ImportAutoConfiguration(classes = SecurityConfig.class)
public class LostPasswordControllerInvalidInputIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LostPasswordService lostPasswordService;
    @MockBean
    private UserDetailsService userDetailsService;

    static final String SAMPLE_HASH = "sample_hash";

    @ParameterizedTest
    @MethodSource("incorrectChangePasswordProvider")
    public void shouldReturnStatus400_createUserInvalidData(ChangedPassword changedPassword, Map<String, String> expectedMessageMap) throws Exception {
        //given
        final String jsonChangePassword = objectMapper.writeValueAsString(changedPassword);
        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(CHANGE_PASSWORD_URL)
                        .content(jsonChangePassword)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andReturn();

        //then
        var actualMessageMap = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Map.class);
        assertEquals(expectedMessageMap, actualMessageMap);
    }

    static Stream<Arguments> incorrectChangePasswordProvider() {

        ChangedPassword changedPasswordInvalidPassword = ChangedPassword.builder()
                .password("Newpassword1234TooLong")
                .confirmPassword("Newpassword1234TooLong")
                .hash(SAMPLE_HASH)
                .build();

        ChangedPassword changedPasswordPasswordsDoNotMatch = ChangedPassword.builder()
                .password("Ne_1")
                .confirmPassword("New_password1234")
                .hash(SAMPLE_HASH)
                .build();

        ChangedPassword changedPasswordPasswordsDoNotMatchOneEmpty = ChangedPassword.builder()
                .password("")
                .confirmPassword("New_password1234")
                .hash(SAMPLE_HASH)
                .build();

        ChangedPassword changedPasswordNullPassword = ChangedPassword.builder()
                .hash(SAMPLE_HASH)
                .build();


        return Stream.of(
                Arguments.of(changedPasswordInvalidPassword,
                        Map.of("password", "password must be no more than 16 characters in length, password must contain 1 or more special characters",
                                "confirmPassword", "password must be no more than 16 characters in length, password must contain 1 or more special characters")
                ),
                Arguments.of(changedPasswordPasswordsDoNotMatch,
                        Map.of("passwordAndConfirmPasswordMatching", "passwords do not match",
                                "password","password must be 6 or more characters in length")
                ),
                Arguments.of(changedPasswordPasswordsDoNotMatchOneEmpty,
                        Map.of("passwordAndConfirmPasswordMatching", "passwords do not match",
                                "password", "password must not be empty")
                ),
                Arguments.of(changedPasswordNullPassword,
                        Map.of("password", "password must not be null",
                                "confirmPassword", "password must not be null")
                )
        );
    }

}
