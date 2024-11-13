package com.project.hotelBooking.controller.model;

import com.project.hotelBooking.controller.Annotations.ValidPassword;
import com.project.hotelBooking.controller.utils.PasswordUtils;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import static com.project.hotelBooking.controller.utils.PasswordUtils.PASSWORDS_DO_NOT_MATCH_MESSAGE;

@Getter
@ToString(callSuper = true)
@SuperBuilder
public class UserCreateDto extends UserBaseDto {

    @ValidPassword
    String password;
    @ValidPassword
    String confirmPassword;

    @AssertTrue(message = PASSWORDS_DO_NOT_MATCH_MESSAGE)
    public boolean isPasswordAndConfirmPasswordMatching() {
        return PasswordUtils.isPasswordAndConfirmPasswordMatching(password, confirmPassword);
    }
}