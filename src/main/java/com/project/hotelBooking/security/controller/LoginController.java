package com.project.hotelBooking.security.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.project.hotelBooking.repository.UserRepository;
import com.project.hotelBooking.repository.model.User;
import com.project.hotelBooking.security.exceptions.InvalidLoginCredentialsException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@CrossOrigin(origins = "*")
public class LoginController {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    private static final String INVALID_USERNAME_OR_PASSWORD_MESSAGE = "Invalid username or password";
    private static final String AUTHENTICATION_FAILED_MESSAGE = "Authentication failed";
    @Value("${jwt.expirationTime}")
    private final long expirationTime;
    @Value("${jwt.secret}")
    private final String secret;

    public LoginController(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           @Value("${jwt.expirationTime}") long expirationTime,
                           @Value("${jwt.secret}") String secret) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.expirationTime = expirationTime;
        this.secret = secret;
    }

    @PostMapping("/login")
    public Token login(@RequestBody LoginCredentials loginCredentials) {
        return authenticate(loginCredentials.getUsername(), loginCredentials.getPassword());
    }

    private Token authenticate(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(
                ()-> new InvalidLoginCredentialsException(INVALID_USERNAME_OR_PASSWORD_MESSAGE)
        );

        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), password)
            );
        } catch (BadCredentialsException e) {
            throw new InvalidLoginCredentialsException(INVALID_USERNAME_OR_PASSWORD_MESSAGE);
        } catch (AuthenticationException ex) {
            throw new InvalidLoginCredentialsException(AUTHENTICATION_FAILED_MESSAGE);
    }

        User principal = (User) authenticate.getPrincipal();
        String token = JWT.create()
                .withSubject(String.valueOf(principal.getUsername()))
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(Algorithm.HMAC256(secret));

        return new Token(token, principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
               // .filter(s -> UserRole.ROLE_ADMIN.name().equals(s))
                .filter(ROLE_ADMIN::equals)
                .map(s -> true)
                .findFirst()
                .orElse(false));
    }

    @Getter
    public static class LoginCredentials {
        private String username;
        private String password;
    }

    @Getter
    @AllArgsConstructor
    public static class Token {
        private String token;
        private boolean adminAccess;
    }
}
