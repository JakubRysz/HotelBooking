package com.project.hotelBooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelBooking.domain.User;
import com.project.hotelBooking.repository.UserRepository;
import com.project.hotelBooking.service.Mail;
import com.project.hotelBooking.service.SimpleEmailService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserControllerTestSuite {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final MockMvc mockMvc;
    private final User newUser = new User();
    @Value("${email_test}")
    private String EMAIL_TEST;
    @MockBean
    private SimpleEmailService emailService;

    @BeforeEach
    private void initialize() {
        newUser.setFirstName("Paul");
        newUser.setLastName("Smith");
        newUser.setDateOfBirth(LocalDate.of(1991,2,16));
        newUser.setUsername("paulsmith");
        newUser.setPassword("paulsmith123");
        newUser.setRole("ROLE_USER");
        newUser.setEmail(EMAIL_TEST);
        userRepository.save(newUser);
        doNothing().when(emailService).send(any(Mail.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldCreateUser() throws Exception {

        //given
        userRepository.delete(newUser);
        final String jsonContentNewUser = objectMapper.writeValueAsString(newUser);
        int usersNumberBefore = userRepository.findAllUsers(Pageable.unpaged()).size();
        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/v1/users")
                        .content(jsonContentNewUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        User user = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);
        int usersNumberAfter = userRepository.findAllUsers(Pageable.unpaged()).size();
        assertEquals(newUser.getFirstName(), user.getFirstName());
        assertEquals(newUser.getLastName(), user.getLastName());
        assertEquals(newUser.getDateOfBirth(), user.getDateOfBirth());
        assertEquals(newUser.getUsername(), user.getUsername());
        assertEquals(newUser.getRole(), user.getRole());
        assertEquals(newUser.getEmail(), user.getEmail());
        assertEquals(usersNumberBefore+1, usersNumberAfter);

        userRepository.delete(newUser);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403CreateUserUser() throws Exception {

        //given
        final String jsonContentNewUser = objectMapper.writeValueAsString(newUser);
        userRepository.delete(newUser);
        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/users")
                        .content(jsonContentNewUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

    @Test
    public void shouldCreateUserRegistrationUser() throws Exception {

        //given
        userRepository.deleteAll();
        userRepository.delete(newUser);
        final String jsonContentNewUser = objectMapper.writeValueAsString(newUser);
        int usersNumberBefore = userRepository.findAllUsers(Pageable.unpaged()).size();
        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/v1/users/registration")
                        .content(jsonContentNewUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        User user = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);
        int usersNumberAfter = userRepository.findAllUsers(Pageable.unpaged()).size();
        assertEquals(newUser.getFirstName(), user.getFirstName());
        assertEquals(newUser.getLastName(), user.getLastName());
        assertEquals(newUser.getDateOfBirth(), user.getDateOfBirth());
        assertEquals(newUser.getUsername(), user.getUsername());
        assertEquals(newUser.getEmail(), user.getEmail());
        assertEquals("ROLE_USER", user.getRole());
        assertEquals(usersNumberBefore+1, usersNumberAfter);

        userRepository.delete(newUser);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldGetSingleUser() throws Exception {

        //given

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/bookings/"+newUser.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        User user = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);

        assertEquals(newUser.getId(), user.getId());
        assertEquals(newUser.getFirstName(), user.getFirstName());
        assertEquals(newUser.getLastName(), user.getLastName());
        assertEquals(newUser.getDateOfBirth(), user.getDateOfBirth());
        assertEquals(newUser.getUsername(), user.getUsername());
        assertEquals(newUser.getEmail(), user.getEmail());
        assertEquals(newUser.getRole(), user.getRole());

        userRepository.delete(newUser);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403GetSingleUserUser() throws Exception {

        //given

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/bookings/"+newUser.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        userRepository.delete(newUser);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldGetMultipleUsers() throws Exception {

        //given
        User newUser2 = new User();
        newUser2.setFirstName("Jan");
        newUser2.setLastName("Kowalski");
        newUser2.setDateOfBirth(LocalDate.of(1992,3,15));
        newUser2.setUsername("jankowalski");
        newUser2.setPassword("jankowalski123");
        newUser2.setRole("ROLE_USER");
        newUser2.setEmail(EMAIL_TEST);
        User newUser3 = new User();
        newUser3.setFirstName("Cris");
        newUser3.setLastName("Brown");
        newUser3.setDateOfBirth(LocalDate.of(1993,4,15));
        newUser3.setUsername("crisbrown");
        newUser3.setPassword("crisbrown123");
        newUser3.setRole("ROLE_USER");
        newUser3.setEmail(EMAIL_TEST);
        userRepository.save(newUser2);
        userRepository.save(newUser3);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/bookings/"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        //then
        User[] usersArray=objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User[].class);
        List<User> users =new ArrayList<>((Arrays.asList(usersArray)));

        assertEquals(3, users.size());
        assertEquals(newUser3.getId(), users.get(2).getId());
        assertEquals(newUser3.getFirstName(), users.get(2).getFirstName());
        assertEquals(newUser3.getLastName(), users.get(2).getLastName());
        assertEquals(newUser3.getDateOfBirth(), users.get(2).getDateOfBirth());
        assertEquals(newUser3.getUsername(), users.get(2).getUsername());
        assertEquals(newUser3.getRole(), users.get(2).getRole());
        assertEquals(newUser3.getEmail(), users.get(2).getEmail());

        userRepository.delete(newUser);
        userRepository.delete(newUser2);
        userRepository.delete(newUser3);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403GetMultipleUsersUser() throws Exception {

        //given
        User newUser2 = new User();
        newUser2.setFirstName("Jan");
        newUser2.setLastName("Kowalski");
        newUser2.setDateOfBirth(LocalDate.of(1992,3,15));
        newUser2.setUsername("jankowalski");
        newUser2.setPassword("jankowalski123");
        newUser2.setRole("ROLE_USER");
        newUser2.setEmail(EMAIL_TEST);
        User newUser3 = new User();
        newUser3.setFirstName("Cris");
        newUser3.setLastName("Brown");
        newUser3.setDateOfBirth(LocalDate.of(1993,4,15));
        newUser3.setUsername("crisbrown");
        newUser3.setPassword("crisbrown123");
        newUser3.setRole("ROLE_USER");
        newUser3.setEmail(EMAIL_TEST);
        userRepository.save(newUser2);
        userRepository.save(newUser3);

        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/bookings/"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        userRepository.delete(newUser);
        userRepository.delete(newUser2);
        userRepository.delete(newUser3);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldEditUser() throws Exception {

        //given
        User userEdited = new User();
        userEdited.setId(newUser.getId());
        userEdited.setFirstName("Paul");
        userEdited.setLastName("Smith");
        userEdited.setDateOfBirth(LocalDate.of(1982,2,16));
        userEdited.setUsername("paulsmith");
        userEdited.setPassword("paulsmith123");
        userEdited.setRole("ROLE_USER");
        userEdited.setEmail(EMAIL_TEST);
        final String jsonContentUserEdited = objectMapper.writeValueAsString(userEdited);
        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/v1/users")
                        .content(jsonContentUserEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn();

        User userGet = userRepository.findById(userEdited.getId()).orElseThrow();

        //then
        User user = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);
        assertEquals(userEdited.getId(), user.getId());
        assertEquals(userEdited.getFirstName(), user.getFirstName());
        assertEquals(userEdited.getLastName(), user.getLastName());
        assertEquals(userEdited.getDateOfBirth(), user.getDateOfBirth());
        assertEquals(userEdited.getUsername(), user.getUsername());
        assertEquals(userEdited.getEmail(), user.getEmail());
        assertEquals(userEdited.getRole(), user.getRole());

        userRepository.delete(user);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldEReturnStatus403ditUserUser() throws Exception {

        //given
        User userEdited = new User();
        userEdited.setId(newUser.getId());
        userEdited.setFirstName("Paul");
        userEdited.setLastName("Smith");
        userEdited.setDateOfBirth(LocalDate.of(1982,2,16));
        userEdited.setUsername("paulsmith");
        userEdited.setPassword("paulsmith123");
        userEdited.setRole("ROLE_USER");
        userEdited.setEmail(EMAIL_TEST);
        final String jsonContentUserEdited = objectMapper.writeValueAsString(userEdited);
        //when
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/users")
                        .content(jsonContentUserEdited)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        userRepository.delete(newUser);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldDeleteUser() throws Exception {

        //given
        int usersNumberBefore=userRepository.findAllUsers(Pageable.unpaged()).size();
        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/users/"+newUser.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));

        int usersNumberAfter=userRepository.findAllUsers(Pageable.unpaged()).size();

        //then
        assertEquals(usersNumberBefore-1, usersNumberAfter);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void shouldReturnStatus403DeleteUserUser() throws Exception {

        //given

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/users/"+newUser.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403));

        userRepository.delete(newUser);
    }
}
