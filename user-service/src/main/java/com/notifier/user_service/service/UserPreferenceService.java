package com.notifier.user_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifier.user_service.dto.UserPreferenceDto;
import com.notifier.user_service.model.User;
import com.notifier.user_service.model.UserPreference;
import com.notifier.user_service.repository.UserPreferenceRepository;
import com.notifier.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPreferenceService {

    private final UserPreferenceRepository userPreferenceRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    @Autowired
    public UserPreferenceService(UserPreferenceRepository userPreferenceRepository,
                                 UserRepository userRepository,
                                 ObjectMapper objectMapper) {
        this.userPreferenceRepository = userPreferenceRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public UserPreferenceDto getUserPreferences(Long userId) {
        return objectMapper.convertValue(userPreferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User preferences not found for user id: " + userId)), UserPreferenceDto.class);
    }

    public UserPreferenceDto getUserPreferencesByUsername(String username) {
        // Fetch user from the database using the username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found for username: " + username));

        // Assuming preferences are directly associated with the user, retrieve the preferences
        return objectMapper.convertValue(user.getUserPreference(), UserPreferenceDto.class);
    }

    @Transactional
    public UserPreference updateUserPreferences(Long userId, boolean emailEnabled, boolean smsEnabled, boolean pushEnabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found for user id: " + userId));

        UserPreference userPreference = user.getUserPreference();
        if (userPreference == null) {
            userPreference = new UserPreference(emailEnabled, smsEnabled, pushEnabled, user);
            user.setUserPreference(userPreference);
        } else {
            userPreference.setEmailNotificationEnabled(emailEnabled);
            userPreference.setSmsNotificationEnabled(smsEnabled);
            userPreference.setPushNotificationEnabled(pushEnabled);
        }

        return userPreferenceRepository.save(userPreference);
    }
}