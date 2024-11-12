package com.project.hotelBooking.controller.model;

import com.project.hotelBooking.controller.Annotations.ValidPassword;
import com.project.hotelBooking.controller.utils.PasswordUtils;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

import static com.project.hotelBooking.controller.utils.PasswordUtils.PASSWORDS_DO_NOT_MATCH_MESSAGE;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class UserCreateBaseDto {
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    String username;
    @ValidPassword
    String password;
    @ValidPassword
    String confirmPassword;
    String email;

    @AssertTrue(message = PASSWORDS_DO_NOT_MATCH_MESSAGE)
    public boolean isPasswordAndConfirmPasswordMatching() {
        return PasswordUtils.isPasswordAndConfirmPasswordMatching(password, confirmPassword);
    }
}