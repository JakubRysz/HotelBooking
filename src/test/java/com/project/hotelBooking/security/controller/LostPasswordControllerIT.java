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
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.project.hotelBooking.common.CommonDatabaseProvider.USER_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class LostPasswordControllerIT {

    private static final String NOT_EXISTING_EMAIL = "test@gmail.com";
    private static final String MAIL_SUBJECT_RESET_PASSWORD = "Hotel booking - reset password request";
    private static final String LOST_PASSWORD_PATH = "/lostPassword";
    private static final String CHANGE_PASSWORD_PATH = "/changePassword";
    private static final String NEW_PASSWORD = "NEW_PASSWORD1234";
    private static final String EXPECTED_EXCEPTION_MESSAGE_NOT_EXISTING_EMAIL =
            "Resource not found: There is no user with given email";

    private static final String EXPECTED_EXCEPTION_MESSAGE_PASSWORDS_NOT_MATCH =
            "Bad request: Given passwords must be the same";
    private static final String EXPECTED_EXCEPTION_MESSAGE_INVALID_HASH =
            "Bad request: Invalid hash to change password";

    private static final String EXPECTED_EXCEPTION_MESSAGE_HASH_EXPIRED =
            "Hash to change password is expired";

    @Value("${app.passwordResetLinkValidityMinutes}")
    private int passwordResetLinkValidityMinutes;

    private final PasswordEncoder passwordEncoder;
    @Value("${app.serviceAddress}")
    private String serviceAddress;

    @Autowired
    CommonDatabaseUtils commonDatabaseUtils;

    private final LostPasswordService lostPasswordService;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    @SpyBean
    private Clock clock;
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
    void lostPassword_shouldSendEmailWithLink_whenEmailExistsInDatabase() throws Exception {
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
    void lostPassword_shouldReturn404Response_whenEmailNotExistsInDatabase() throws Exception {
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
    void changePassword_shouldChangePassword_whenValidRequest() throws Exception {
        // given
        String hashFromMail = performLostPasswordRequestAndGetHashFromMail(USER_1.getEmail());
        
        String changePasswordJson = objectMapper.writeValueAsString(
                ChangedPassword.builder()
                        .password(NEW_PASSWORD)
                        .repeatPassword(NEW_PASSWORD)
                        .hash(hashFromMail)
                        .build()
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders.post(CHANGE_PASSWORD_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(changePasswordJson)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        User user = userRepository.findTopByEmail(USER_1.getEmail()).orElseThrow();

        // then
        assertTrue(passwordEncoder.matches(NEW_PASSWORD, user.getPassword()));
    }

    @Test
    void changePassword_shouldReturn400Response_whenNewPasswordsNotMatch() throws Exception {
        // given
        String hashFromMail = performLostPasswordRequestAndGetHashFromMail(USER_1.getEmail());

        String changePasswordJson = objectMapper.writeValueAsString(
                ChangedPassword.builder()
                        .password(NEW_PASSWORD)
                        .repeatPassword("different_password")
                        .hash(hashFromMail)
                        .build()
        );

        // when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(CHANGE_PASSWORD_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(changePasswordJson)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andReturn();

        // then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(EXPECTED_EXCEPTION_MESSAGE_PASSWORDS_NOT_MATCH, responseMessage);
    }

    @Test
    void changePassword_shouldReturn400Response_whenHashIsInvalid() throws Exception {
        // given
        String invalidHash = DigestUtils.sha256Hex(USER_1.getId() + USER_1.getUsername() + USER_1.getPassword()
                + LocalDateTime.now());
        String changePasswordJson = objectMapper.writeValueAsString(
                ChangedPassword.builder()
                        .password(NEW_PASSWORD)
                        .repeatPassword(NEW_PASSWORD)
                        .hash(invalidHash)
                        .build()
        );

        // when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(CHANGE_PASSWORD_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(changePasswordJson)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andReturn();

        // then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(EXPECTED_EXCEPTION_MESSAGE_INVALID_HASH, responseMessage);
    }

    @Test
    void changePassword_shouldReturn400Response_whenHashIsExpired() throws Exception {
        // given
        String hashFromMail = performLostPasswordRequestAndGetHashFromMail(USER_1.getEmail());

        Instant baseInstant = Instant.now().plus(passwordResetLinkValidityMinutes + 1, ChronoUnit.MINUTES);
        Clock fixedClock = Clock.fixed(baseInstant, ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());

        String changePasswordJson = objectMapper.writeValueAsString(
                ChangedPassword.builder()
                        .password(NEW_PASSWORD)
                        .repeatPassword(NEW_PASSWORD)
                        .hash(hashFromMail)
                        .build()
        );

        // when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(CHANGE_PASSWORD_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(changePasswordJson)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(410))
                .andReturn();

        // then
        String responseMessage = mvcResult.getResponse().getContentAsString();
        assertEquals(EXPECTED_EXCEPTION_MESSAGE_HASH_EXPIRED, responseMessage);
    }

    private static String getHashFromMessage(String message) {
        String regex = "([a-f0-9]{64})";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(message);
        matcher.find();
        String hash = matcher.group(1);
        return hash;
    }

    private String performLostPasswordRequestAndGetHashFromMail(String email) throws Exception {
        String emailJson = objectMapper.writeValueAsString(
                new EmailDto(email)
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
        return getHashFromMessage(mailToBeSend.getMessage());
    }
}