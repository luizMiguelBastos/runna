package com.luizMiguel.runna.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.UUID;

@Entity
@Table(name = "runna_exercises")
@Getter
@Setter
public class ExerciseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private enum ExerciseType {
        BIKE,
        RUNNING,
        WALKING
    }
    private ExerciseType tipo;
    private Duration duration;
    private double distanceInKm;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    public Duration calcularPace() {
        return duration.dividedBy((long) distanceInKm);
    }

}

