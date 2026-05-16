package com.Questboard.backend.modules.auth.events;


import com.Questboard.backend.modules.auth.model.User;

public record UserRegistrationEvent(
    User user
) {
}
