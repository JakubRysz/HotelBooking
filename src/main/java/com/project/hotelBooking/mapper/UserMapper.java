package com.project.hotelBooking.mapper;

import com.project.hotelBooking.domain.User;
import com.project.hotelBooking.domain.UserDto;
import com.project.hotelBooking.domain.UserWithBookingDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {


    public User mapToUser(UserWithBookingDto userWithBookingDto) {
        return new User(
                userWithBookingDto.getId(),
                userWithBookingDto.getFirstName(),
                userWithBookingDto.getLastName(),
                userWithBookingDto.getDateOfBirth(),
                null,
                null,
                null,
                userWithBookingDto.getBookings()
        );
    }
    public UserWithBookingDto mapToUserWithBookingDto(User user) {
        return new UserWithBookingDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getDateOfBirth(),
                user.getBookings()
        );
    }

    public UserDto mapToUserDto(User user) {
            return new UserDto(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getDateOfBirth(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getRole()
            );
    }
    public User mapToUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getDateOfBirth(),
                userDto.getUsername(),
                userDto.getPassword(),
                userDto.getRole(),
                null
        );
    }
}
