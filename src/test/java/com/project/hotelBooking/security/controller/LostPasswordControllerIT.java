package com.project.hotelBooking.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.common.CommonDatabaseUtils;
import com.project.hotelBooking.repository.UserRepository;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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
        String mailMessageRegex = String.format(
                "Please click link below to reset your password:\n\n%s%s/[a-fA-F0-9]{64}\n\nThank you",
                serviceAddress, LOST_PASSWORD_PATH
        );
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
}