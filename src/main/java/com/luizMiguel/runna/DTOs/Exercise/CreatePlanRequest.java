package com.luizMiguel.runna.DTOs.Exercise;

import com.luizMiguel.runna.Models.ExerciseModel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePlanRequest(
        @NotNull ExerciseModel.ExerciseType type,
        @NotNull @Positive Double targetDistanceKm,
        @NotNull @Min(1) @Max(16) Integer weeks
) {
}
