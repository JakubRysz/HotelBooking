package com.project.hotelBooking.controller.mapper;

import com.project.hotelBooking.controller.model.UserDto;
import com.project.hotelBooking.controller.model.UserWithBookingDto;
import com.project.hotelBooking.service.model.UserServ;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserWithBookingDto mapToUserWithBookingDto(UserServ user);
    UserDto mapToUserDto(UserServ user);
    UserServ mapToUser(UserDto userDto);
}
