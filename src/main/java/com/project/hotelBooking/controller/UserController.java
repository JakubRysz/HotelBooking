package com.project.hotelBooking.controller;

import com.project.hotelBooking.controller.exceptions.BadRequestException;
import com.project.hotelBooking.controller.mapper.UserMapper;
import com.project.hotelBooking.controller.model.UserCreateDto;
import com.project.hotelBooking.controller.model.UserCreateAdminDto;
import com.project.hotelBooking.controller.model.UserDto;
import com.project.hotelBooking.controller.model.UserWithBookingDto;
import com.project.hotelBooking.service.SimpleEmailService;
import com.project.hotelBooking.service.UserService;
import com.project.hotelBooking.service.model.UserServ;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Validated
public class UserController {

    private static final String USER_ROLE = "ROLE_USER";

    private final UserService userService;
    private final UserMapper userMapper;
    private final ValidatorCustom validatorCustom;
    private final SimpleEmailService emailService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users")
    public UserDto createUser(@Valid @RequestBody UserCreateAdminDto userDto) {
        UserServ user = userMapper.mapToUser(userDto);
        validatorCustom.validateUser(user);
        UserDto userCreated = userMapper.mapToUserDto(userService.saveUser(user));
        emailService.sendMailCreatedUser(user);
        return userCreated;
    }

    @PostMapping("/users/registration")
    public UserDto createUserRegistration(@Valid @RequestBody UserCreateDto userDto) {
        UserServ user = userMapper.mapToUser(userDto);
        user = user.withRole(USER_ROLE);
        validatorCustom.validateUser(user);
        UserDto userCreated = userMapper.mapToUserDto(userService.saveUser(user));
        emailService.sendMailCreatedUser(user);
        return userCreated;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/bookings/{id}")
    public UserWithBookingDto getSingleUser(@PathVariable Long id) {
        return userMapper.mapToUserWithBookingDto(userService.getUserById(id));
    }

    @GetMapping("/users/own/bookings")
    public UserWithBookingDto getSingleUserOwner() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userMapper.mapToUserWithBookingDto(userService.getUserByUsername(auth.getName()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/bookings")
    public List<UserWithBookingDto> getUsersWithBookings(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if (page == null || page < 0) page = 0;
        if (sort == null) sort = Sort.Direction.ASC;
        return (userService.getUsersWithBookings(page, sort)
                .stream().map(userMapper::mapToUserWithBookingDto)
                .collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if (page == null || page < 0) page = 0;
        if (sort == null) sort = Sort.Direction.ASC;
        return (userService.getUsers(page, sort)
                .stream().map(userMapper::mapToUserDto)
                .collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users")
    public UserDto editUser(@RequestBody UserDto userDto) {
        UserServ user = userMapper.mapToUser(userDto);
        validatorCustom.validateUserEdit(user);
        UserDto editedUser = userMapper.mapToUserDto(userService.editUser(user));
        emailService.sendMailEditedUser(user);
        return editedUser;
    }

    @PutMapping("/users/own")
    public UserDto editUserUser(@RequestBody UserDto userDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserServ userAuth = userService.getUserByUsername(auth.getName());
        UserServ user = userMapper.mapToUser(userDto);
        if (!Objects.equals(user.getId(), userAuth.getId()))
            throw new BadRequestException("Given user Id is not Id of current authenticated user");
        validatorCustom.validateUserEdit(user);
        UserDto editedUser = userMapper.mapToUserDto(userService.editUser(user));
        emailService.sendMailEditedUser(user);
        return editedUser;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        UserServ userToDelete = userService.getUserById(id);
        userService.deleteUserById(id);
        emailService.sendMailDeletedUser(userToDelete);
    }
}
