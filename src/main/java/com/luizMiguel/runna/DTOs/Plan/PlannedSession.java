package com.luizMiguel.runna.DTOs.Plan;

import com.luizMiguel.runna.Models.ExerciseModel;

public record PlannedSession(
        ExerciseModel.ExerciseType type,
        Double targetDistanceKm,
        String targetPace,
        String observation
) {
}
