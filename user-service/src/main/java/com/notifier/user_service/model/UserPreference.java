package com.notifier.user_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private boolean emailNotificationEnabled;

    @NotNull
    private boolean smsNotificationEnabled;

    @NotNull
    private boolean pushNotificationEnabled;

    @OneToOne
    @JsonBackReference
    private User user;

    public UserPreference() {
    }
    public UserPreference(boolean emailEnabled, boolean smsEnabled, boolean pushEnabled, User user) {
        this.emailNotificationEnabled = emailEnabled;
        this.smsNotificationEnabled = smsEnabled;
        this.pushNotificationEnabled = pushEnabled;
        this.user = user;
    }
}
