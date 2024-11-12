package com.project.hotelBooking.controller.Annotations;

import com.project.hotelBooking.controller.utils.PasswordConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PasswordConstraintValidatorTest {
    private PasswordConstraintValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    public void setUp() {
        validator = new PasswordConstraintValidator();
        context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    public void testValidPassword() {
        assertTrue(validator.isValid("Valid123!", context));
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectPasswords")
    public void testInvalidPasswords(String password, String expectedMessage) {
        assertFalse(validator.isValid(password, context));
        verify(context).buildConstraintViolationWithTemplate(expectedMessage);
    }

    private static Stream<Arguments> provideIncorrectPasswords() {
        return Stream.of(
                Arguments.of(null, "password must not be null"),
                Arguments.of("Sho1!", "password must be 6 or more characters in length"),
                Arguments.of("lowercase123!", "password must contain 1 or more uppercase characters"),
                Arguments.of("UPPERCASE123!", "password must contain 1 or more lowercase characters"),
                Arguments.of("NoSpecial123", "password must contain 1 or more special characters"),
                Arguments.of("Whitespace 123!", "password contains a whitespace character"),
                Arguments.of("a", "password must be 6 or more characters in length, password must contain 1 " +
                        "or more uppercase characters, password must contain 1 or more digit characters, " +
                        "password must contain 1 or more special characters")
        );
    }
}