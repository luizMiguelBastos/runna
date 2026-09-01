package com.luizMiguel.runna.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "runna_exercises")
@Getter
@Setter
public class ExerciseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    public enum ExerciseType {BIKE, RUN, WALK}

    @Enumerated(EnumType.STRING)
    private ExerciseType type;


    private Duration duration;
    private double distanceInKm;
    private Duration pace;
    @CreationTimestamp
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    public void paceCalc() {
        if (distanceInKm <= 0) {
            throw new IllegalArgumentException("Distance must be greater than zero!");
        }

        long seconds = duration.toSeconds();
        long paceEmSegundos = Math.round(seconds / distanceInKm);

        this.pace = Duration.ofSeconds(paceEmSegundos);
    }

}