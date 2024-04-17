package com.dyma.tennis;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record Rank(
        @Positive(message = "Position must be a positive number") int position,
        @PositiveOrZero(message = "Points must be more than zero") int points
) {
}
