package com.project.hotelBooking.service.mapper;

import com.project.hotelBooking.repository.model.User;
import com.project.hotelBooking.service.model.UserServ;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapperServ {
    UserServ mapToUser(User user);
    List<UserServ> mapToUsers(List<User> user);
    User mapToUserRepository(UserServ user);
}
