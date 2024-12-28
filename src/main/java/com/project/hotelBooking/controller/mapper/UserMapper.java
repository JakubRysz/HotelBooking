package com.project.hotelBooking.controller.mapper;

import com.project.hotelBooking.controller.model.user.*;
import com.project.hotelBooking.service.model.UserServ;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserWithBookingsDto mapToUserWithBookingDto(UserServ user);
    UserDto mapToUserDto(UserServ user);
    UserServ mapToUser(UserEditDto userDto);
    UserServ mapToUser(UserDto userDto);
    UserServ mapToUser(UserCreateDto userDto);
    UserServ mapToUser(UserCreateAdminDto userDto);
}
