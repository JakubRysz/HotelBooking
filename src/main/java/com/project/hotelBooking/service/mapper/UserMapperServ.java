package com.project.hotelBooking.service.mapper;

import com.project.hotelBooking.service.model.UserServ;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapperServ {
    UserServ mapToUser(com.project.hotelBooking.repository.model.User user);
}
