package com.luizMiguel.runna.DTOs.Exercise;

import com.luizMiguel.runna.Models.ExerciseModel;

import java.time.Duration;

public record CreateExerciseRequest(ExerciseModel.ExerciseType type, Duration duration, double distanceInKm) {
}
