package com.dyma.tennis;

import jakarta.validation.constraints.NotBlank;

public record UserCredentials(
        @NotBlank(message = "Login is mandatory") String login,
        @NotBlank(message = "Password is mandatory") String password) {
}
