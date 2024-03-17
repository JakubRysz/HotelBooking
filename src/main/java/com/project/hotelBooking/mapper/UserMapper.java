package com.project.hotelBooking.mapper;

import com.project.hotelBooking.repository.model.User;
import com.project.hotelBooking.controller.model.UserDto;
import com.project.hotelBooking.controller.model.UserWithBookingDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserWithBookingDto mapToUserWithBookingDto(User user);
    UserDto mapToUserDto(User user);
    User mapToUser(UserDto userDto);
}
