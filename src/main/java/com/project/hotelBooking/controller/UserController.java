package com.project.hotelBooking.controller;

import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.domain.User;
import com.project.hotelBooking.domain.UserDto;
import com.project.hotelBooking.domain.UserWithBookingDto;
import com.project.hotelBooking.mapper.UserMapper;
import com.project.hotelBooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private Validator validator;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users")
    public UserDto createUser(@RequestBody UserDto userDto) {
        User user = userMapper.mapToUser(userDto);
        validator.validateUser(user);
        return userMapper.mapToUserDto(userService.saveUser(userMapper.mapToUser(userDto)));
    };
    @PostMapping("/users/registration")
    public UserDto createUserRegistration(@RequestBody UserDto userDto) {
        userDto.setRole("ROLE_USER");
        User user = userMapper.mapToUser(userDto);
        validator.validateUser(user);
        return userMapper.mapToUserDto(userService.saveUser(userMapper.mapToUser(userDto)));
    };
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/Bookings/{id}")
    public UserWithBookingDto getSingleUser(@PathVariable Long id) {
        return userMapper.mapToUserWithBookingDto(userService.getUserById(id)
                .orElseThrow(()->new ElementNotFoundException("No such user")));
    }
    @GetMapping("/users/own/Bookings/")
    public UserWithBookingDto getSingleUserOwner() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userMapper.mapToUserWithBookingDto(userService.getUserByUsername(auth.getName()));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/Bookings")
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
        return userMapper.mapToUserDto(userService.editUser(user));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        validator.validateIfUserExistById(id);
        userService.deleteUserById(id);
    }

}
