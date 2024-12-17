package com.project.hotelBooking.controller.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor
@SuperBuilder
public abstract class UserBaseDto {
    @JsonProperty
    String firstName;
    @JsonProperty
    String lastName;
    @JsonProperty
    LocalDate dateOfBirth;
    @JsonProperty
    String username;
    @JsonProperty
    String email;
}
