package com.project.hotelBooking.service;

import com.project.hotelBooking.controller.exceptions.ElementNotFoundException;
import com.project.hotelBooking.domain.Booking;
import com.project.hotelBooking.domain.User;
import com.project.hotelBooking.mapper.HotelMapper;
import com.project.hotelBooking.mapper.RoomMapper;
import com.project.hotelBooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final int PAGE_SIZE=5;

    public Optional<User> getUserByUsername(String username) { return userRepository.findByUsername(username);}
    public Optional<User> getUserByEmail(String email) { return userRepository.findTopByEmail(email);}
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
    public List<User> getUsers(Integer page, Sort.Direction sort) {
        return userRepository.findAllUsers(PageRequest.of(page,PAGE_SIZE, Sort.by(sort, "id")));
    }
    public List<User> getUsersWithBookings(Integer page, Sort.Direction sort) {
        List<User> users =  userRepository.findAllUsers(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")));
        List<Long> ids = users.stream().map(User::getId).collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findAllByUserIdIn(ids);
        users.forEach(user -> user.setBookings(extractBookingsUser(bookings, user.getId())));
        return users;
    }
    private List<Booking> extractBookingsUser(List<Booking> bookings, Long id) {
        return bookings.stream()
                .filter(booking -> booking.getUserId()==id).collect(Collectors.toList());
    }
    public User editUser(User user) {
        return saveUser(user);
    }
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

}
