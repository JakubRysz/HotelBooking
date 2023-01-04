package com.project.hotelBooking.controller;

import com.project.hotelBooking.controller.exceptions.BadRequestException;
import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.domain.User;
import com.project.hotelBooking.domain.UserDto;
import com.project.hotelBooking.domain.UserWithBookingDto;
import com.project.hotelBooking.mapper.UserMapper;
import com.project.hotelBooking.service.SimpleEmailService;
import com.project.hotelBooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final Validator validator;
    private final SimpleEmailService emailService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users")
    public UserDto createUser(@RequestBody UserDto userDto) {
        User user = userMapper.mapToUser(userDto);
        validator.validateUser(user);
        UserDto userCreated = userMapper.mapToUserDto(userService.saveUser(userMapper.mapToUser(userDto)));
        emailService.sendMailCreatedUser(user);
        return userCreated;
    };
    @PostMapping("/users/registration")
    public UserDto createUserRegistration(@RequestBody UserDto userDto) {
        userDto.setRole("ROLE_USER");
        User user = userMapper.mapToUser(userDto);
        validator.validateUser(user);
        UserDto userCreated = userMapper.mapToUserDto(userService.saveUser(userMapper.mapToUser(userDto)));
        emailService.sendMailCreatedUser(user);
        return userCreated;
    };
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/bookings/{id}")
    public UserWithBookingDto getSingleUser(@PathVariable Long id) {
        return userMapper.mapToUserWithBookingDto(userService.getUserById(id)
                .orElseThrow(()->new ElementNotFoundException("No such user")));
    }
    @GetMapping("/users/own/bookings")
    public UserWithBookingDto getSingleUserOwner() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userMapper.mapToUserWithBookingDto(userService.getUserByUsername(auth.getName()).orElseThrow(
                ()-> new ElementNotFoundException("No such user")));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/bookings")
    public List<UserWithBookingDto> getUsersWithBookings(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if(page==null||page<0) page=0;
        if (sort==null) sort=Sort.Direction.ASC;
        return (userService.getUsersWithBookings(page, sort)
                .stream().map(k -> userMapper.mapToUserWithBookingDto(k))
                .collect(Collectors.toList()));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        if(page==null||page<0) page=0;
        if (sort==null) sort=Sort.Direction.ASC;
        return (userService.getUsers(page, sort)
                .stream().map(k -> userMapper.mapToUserDto(k))
                .collect(Collectors.toList()));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users")
    public UserDto editUser(@RequestBody UserDto userDto) {
        User user = userMapper.mapToUser(userDto);
        validator.validateUserEdit(user);
        UserDto editedUser = userMapper.mapToUserDto(userService.editUser(user));
        emailService.sendMailEditedUser(user);
        return editedUser;
    }
    @PutMapping("/users/own")
    public UserDto editUserUser(@RequestBody UserDto userDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userAuth = userService.getUserByUsername(auth.getName()).orElseThrow(
                ()-> new ElementNotFoundException("No such user"));
        User user = userMapper.mapToUser(userDto);
        if(user.getId()!=userAuth.getId()) throw new BadRequestException("Given user Id is not Id of current authenticated user");
        validator.validateUserEdit(user);
        UserDto editedUser = userMapper.mapToUserDto(userService.editUser(user));
        emailService.sendMailEditedUser(user);
        return editedUser;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        validator.validateIfUserExistById(id);
        User userToDelete = userService.getUserById(id).orElseThrow(()->new ElementNotFoundException("No such user"));
        userService.deleteUserById(id);
        emailService.sendMailDeletedUser(userToDelete);
    }

}
