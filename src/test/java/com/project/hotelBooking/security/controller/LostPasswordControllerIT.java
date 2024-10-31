package com.project.hotelBooking.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.common.CommonDatabaseUtils;
import com.project.hotelBooking.repository.UserRepository;
import com.project.hotelBooking.repository.model.User;
import com.project.hotelBooking.security.model.ChangedPassword;
import com.project.hotelBooking.security.model.EmailDto;
import com.project.hotelBooking.security.service.LostPasswordService;
import com.project.hotelBooking.service.SimpleEmailService;
import com.project.hotelBooking.service.model.Mail;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.project.hotelBooking.common.CommonDatabaseProvider.USER_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class LostPasswordControllerIT {
    private static final String EXPECTED_EXCEPTION_MESSAGE_NOT_EXISTING_EMAIL =
            "Resource not found: There is no user with given email";
    private static final String NOT_EXISTING_EMAIL = "test@gmail.com";
    private static final String MAIL_SUBJECT_RESET_PASSWORD = "Hotel booking - reset password request";
    private static final String LOST_PASSWORD_PATH = "/lostPassword";
    private static final String CHANGE_PASSWORD_PATH = "/changePassword";
    private static final String NEW_PASSWORD = "NEW_PASSWORD1234";
    private final PasswordEncoder passwordEncoder;
    @Value("${app.serviceAddress}")
    private String serviceAddress;

    @Autowired
    CommonDatabaseUtils commonDatabaseUtils;

    private final LostPasswordService lostPasswordService;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    @MockBean
    private SimpleEmailService simpleEmailService;

    @BeforeEach
    public void setUp() {
        userRepository.save(USER_1);
    }

    @AfterEach
    public void cleanUp() {
        commonDatabaseUtils.clearDatabaseTables();
    }

    @Test
    void shouldSendEmailWithLink_whenEmailExistsInDatabase() throws Exception {
        // given
        String mailMessageRegex = "Please find bellow hash to reset your password:\n\n[a-fA-F0-9]{64}\n\nThank you";

        String emailJson = objectMapper.writeValueAsString(
                new EmailDto(USER_1.getEmail())
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders.post(LOST_PASSWORD_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(emailJson)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        ArgumentCaptor<Mail> captor = ArgumentCaptor.forClass(Mail.class);
        verify(simpleEmailService).send(captor.capture());

        // then
        Mail mailToBeSend = captor.getValue();
        assertEquals(MAIL_SUBJECT_RESET_PASSWORD, mailToBeSend.getSubject());
        assertTrue(mailToBeSend.getMessage().matches(mailMessageRegex));
    }

    @Test
    void shouldReturn404Response_whenEmailNotExistsInDatabase() throws Exception {
        // given
        String emailJson = objectMapper.writeValueAsString(
                new EmailDto(NOT_EXISTING_EMAIL)
        );

        // when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(LOST_PASSWORD_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(emailJson)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(404))
                .andReturn();

        // then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(EXPECTED_EXCEPTION_MESSAGE_NOT_EXISTING_EMAIL, responseMessage);
    }

    @Test
    void shouldChangePassword_whenValidRequest() throws Exception {
        // given
        String emailJson = objectMapper.writeValueAsString(
                new EmailDto(USER_1.getEmail())
        );

        mockMvc.perform(MockMvcRequestBuilders.post(LOST_PASSWORD_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(emailJson)
                )
                .andExpect(MockMvcResultMatchers.status().is(200));

        ArgumentCaptor<Mail> captor = ArgumentCaptor.forClass(Mail.class);
        verify(simpleEmailService).send(captor.capture());
        Mail mailToBeSend = captor.getValue();
        
        String changePasswordJson = objectMapper.writeValueAsString(
                ChangedPassword.builder()
                        .password(NEW_PASSWORD)
                        .repeatPassword(NEW_PASSWORD)
                        .hash(getHashFromMessage(mailToBeSend.getMessage()))
                        .build()
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders.post(CHANGE_PASSWORD_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(changePasswordJson)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        User user = userRepository.findTopByEmail(USER_1.getEmail()).orElseThrow();

        // then
        assertTrue(passwordEncoder.matches(NEW_PASSWORD, user.getPassword()));
    }

// Test cases:
//    The password and repeat password do not match.
//    The hash is invalid.
//    The link to change the password has expired.

    private static String getHashFromMessage(String message) {
        String regex = "([a-f0-9]{64})";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(message);
        matcher.find();
        String hash = matcher.group(1);
        return hash;
    }
}