package com.luizMiguel.runna.DTOs.Exercise;

import com.luizMiguel.runna.Models.ExerciseModel;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public record ExerciseResponse(UUID id, ExerciseModel.ExerciseType type, Duration duration, double distanceInKm, Duration pace, Instant createdAt) {
}
