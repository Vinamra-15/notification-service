package com.notifier.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserPreferenceDto {
    @NotNull
    private boolean emailNotificationEnabled;

    @NotNull
    private boolean smsNotificationEnabled;

    @NotNull
    private boolean pushNotificationEnabled;
}
