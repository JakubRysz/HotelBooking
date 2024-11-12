package com.project.hotelBooking.controller.utils;

import com.project.hotelBooking.controller.Annotations.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.passay.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Slf4j
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(final ValidPassword arg0) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            context.buildConstraintViolationWithTemplate("password must not be null")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        }
        if (password.isEmpty()) {
            context.buildConstraintViolationWithTemplate("password must not be empty")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        }

        try (InputStream inputStream = getClass()
                .getClassLoader().getResourceAsStream("passay.properties")) {
            Properties props = new Properties();

            if (inputStream == null) {
                log.error("Passay properties file not found");
                context.buildConstraintViolationWithTemplate("Password validation configuration missing")
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                return false;
            }

            props.load(inputStream);
            PasswordValidator validator = getPasswordValidator(props);
            RuleResult result = validator.validate(new PasswordData(password));

            if (result.isValid()) {
                return true;
            }

            List<String> messages = validator.getMessages(result);
            List<String> messagesWithoutDots = convertMessages(messages);
            String messageTemplate = String.join(", ", messagesWithoutDots);
            context.buildConstraintViolationWithTemplate(messageTemplate)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;

        } catch (IOException e) {
            log.error("Error loading passay properties", e);
            context.buildConstraintViolationWithTemplate("Error reading password rules")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        } catch (Exception e) {
            log.error("Unexpected error during password validation", e);
            context.buildConstraintViolationWithTemplate("An unexpected error occurred during password validation")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        }
    }

    private static List<String> convertMessages(List<String> messages) {
        return messages.stream()
                .map(message -> {
                    String withoutDot = message.endsWith(".") ? message.substring(0, message.length() - 1) : message;
                    return withoutDot.substring(0, 1).toLowerCase() + withoutDot.substring(1);
                })
                .toList();
    }

    private static PasswordValidator getPasswordValidator(Properties props) {
        MessageResolver resolver = new PropertiesMessageResolver(props);
        PasswordValidator validator = new PasswordValidator(resolver, Arrays.asList(
                new LengthRule(6, 16),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new WhitespaceRule()
        ));
        return validator;
    }
}