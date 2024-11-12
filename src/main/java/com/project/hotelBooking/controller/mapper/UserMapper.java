package com.project.hotelBooking.controller.mapper;

import com.project.hotelBooking.controller.model.UserCreateDto;
import com.project.hotelBooking.controller.model.UserCreateAdminDto;
import com.project.hotelBooking.controller.model.UserDto;
import com.project.hotelBooking.controller.model.UserWithBookingDto;
import com.project.hotelBooking.service.model.UserServ;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserWithBookingDto mapToUserWithBookingDto(UserServ user);
    UserDto mapToUserDto(UserServ user);
    UserServ mapToUser(UserDto userDto);
    UserServ mapToUser(UserCreateDto userDto);
    UserServ mapToUser(UserCreateAdminDto userDto);
}
