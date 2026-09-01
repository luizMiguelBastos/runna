package com.luizMiguel.runna.DTOs.Exercise;

import com.luizMiguel.runna.Models.ExerciseModel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public record ExerciseResponse(UUID id, ExerciseModel.ExerciseType type,
                               @Schema(type = "string", example = "PT30M") Duration duration,
                               double distanceInKm, @Schema(type = "string", example = "PT30M")Duration pace,
                               Instant createdAt) {
}
