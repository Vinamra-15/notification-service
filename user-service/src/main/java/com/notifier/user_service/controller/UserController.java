package com.notifier.user_service.controller;

import com.notifier.user_service.config.JwtTokenProvider;
import com.notifier.user_service.dto.UserDto;
import com.notifier.user_service.dto.UserPreferenceDto;
import com.notifier.user_service.service.UserPreferenceService;
import com.notifier.user_service.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8080/")
public class UserController {
    private final UserService userService;
    private final UserPreferenceService userPreferenceService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService,
                          UserPreferenceService userPreferenceService,
                          JwtTokenProvider jwtTokenProvider,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.userPreferenceService = userPreferenceService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Endpoint for user registration.
     *
     * @param userDTO - Data Transfer Object for user registration details
     * @return ResponseEntity with status and result
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto userDTO) {
        try {
            userService.registerUser(userDTO.getUsername(), userDTO.getEmail(), userDTO.getPassword());
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("User registration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint for user login.
     *
     * @param userDTO - Data Transfer Object for login credentials
     * @return JWT token for authentication
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDto userDTO) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDTO.getUsername(),
                            userDTO.getPassword()
                    )
            );

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(authentication);
            return ResponseEntity.ok(token);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred during login");
        }
    }

    /**
     * Endpoint to fetch user notification preferences.
     *
     * @param authorizationHeader - User identifier (JWT token should be used for authentication)
     * @return ResponseEntity with user preferences
     */
    @GetMapping("/preferences")
    public ResponseEntity<UserPreferenceDto> getUserPreferences(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract the JWT token from the Authorization header
            String token = authorizationHeader.split(" ")[1];
            String username = jwtTokenProvider.getUsernameFromToken(token);

            // Fetch user preferences using the extracted user ID
            UserPreferenceDto preferences = userPreferenceService.getUserPreferencesByUsername(username);
            return ResponseEntity.ok(preferences);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to update user notification preferences.
     *
     * @param userId       - User identifier
     * @param preferenceDTO - Data Transfer Object for user preferences
     * @return ResponseEntity with status
     */
    @PutMapping("/{userId}/preferences")
    public ResponseEntity<String> updateUserPreferences(@PathVariable Long userId, @RequestBody UserPreferenceDto preferenceDTO) {
        try {
            userPreferenceService.updateUserPreferences(userId,
                                                        preferenceDTO.isEmailNotificationEnabled(),
                                                        preferenceDTO.isSmsNotificationEnabled(),
                                                        preferenceDTO.isPushNotificationEnabled());
            return ResponseEntity.ok("Preferences updated successfully");
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update preferences: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
