package com.project.hotelBooking.controller.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
public class UserCreateAdminDto extends UserCreateDto {
    String role;
}