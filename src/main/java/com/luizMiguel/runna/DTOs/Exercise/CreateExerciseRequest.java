package com.luizMiguel.runna.DTOs.Exercise;

import com.luizMiguel.runna.Models.ExerciseModel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Duration;

public record CreateExerciseRequest (@NotNull ExerciseModel.ExerciseType type, @NotNull Duration duration, @Positive double distanceInKm) {
}
