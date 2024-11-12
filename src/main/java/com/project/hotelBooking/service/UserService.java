package com.project.hotelBooking.service;

import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.repository.BookingRepository;
import com.project.hotelBooking.repository.UserRepository;
import com.project.hotelBooking.repository.model.User;
import com.project.hotelBooking.service.mapper.BookingMapperServ;
import com.project.hotelBooking.service.mapper.UserMapperServ;
import com.project.hotelBooking.service.model.BookingServ;
import com.project.hotelBooking.service.model.UserServ;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final UserMapperServ userMapperServ;
    private final BookingMapperServ bookingMapperServ;
    private final PasswordEncoder passwordEncoder;
    private static final int PAGE_SIZE = 5;

    public UserServ getUserByUsername(String username) {
        return userMapperServ.mapToUser(
                userRepository.findByUsername(username).orElseThrow(
                        () -> new ElementNotFoundException("No such user"))
        );
    }

    public UserServ saveUser(UserServ user) {
        user = user.withPassword(passwordEncoder.encode(user.getPassword()));
        return userMapperServ.mapToUser(
                userRepository.save(userMapperServ.mapToUserRepository(user))
        );
    }

    public UserServ getUserById(Long id) {
        return userMapperServ.mapToUser(
        userRepository.findById(id).orElseThrow(
                () -> new ElementNotFoundException("No such user"))
        );
    }

    public UserServ getUserByEmail(String email) {
        return userMapperServ.mapToUser(
                userRepository.findTopByEmail(email).orElseThrow(
                        () -> new ElementNotFoundException("No such user"))
        );
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public List<UserServ> getUsers(Integer page, Sort.Direction sort) {
        return userMapperServ.mapToUsers(
                userRepository.findAllUsers(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")))
        );
    }

    public List<UserServ> getUsersWithBookings(Integer page, Sort.Direction sort) {
        List<UserServ> users = userMapperServ.mapToUsers(
                userRepository.findAllUsers(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")))
        );
        List<Long> ids = users.stream().map(UserServ::getId).collect(Collectors.toList());
        List<BookingServ> bookings = bookingMapperServ.mapToBookings(
                bookingRepository.findAllByUserIdIn(ids)
        );
        users.forEach(user -> user.withBookings(extractBookingsUser(bookings, user.getId())));
        return users;
    }

    private List<BookingServ> extractBookingsUser(List<BookingServ> bookings, Long id) {
        return bookings.stream()
                .filter(booking -> booking.getUserId() == id).collect(Collectors.toList());
    }

    public UserServ editUser(UserServ userDto) {
        User existingUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new ElementNotFoundException("No such user"));

        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        existingUser.setDateOfBirth(userDto.getDateOfBirth());
        existingUser.setUsername(userDto.getUsername());
        existingUser.setRole(userDto.getRole());
        existingUser.setEmail(userDto.getEmail());

        return userMapperServ.mapToUser(
                userRepository.save(existingUser)
        );
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

}
