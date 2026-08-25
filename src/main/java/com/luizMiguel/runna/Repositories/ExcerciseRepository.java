package com.luizMiguel.runna.Repositories;

import com.luizMiguel.runna.Models.ExerciseModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExcerciseRepository extends JpaRepository<ExerciseModel, UUID> {

    List<ExerciseModel> findAllByOrderByCreatedAtDesc();

    List<ExerciseModel> findAllByOrderByCreatedAtAsc();

    List<ExerciseModel> findAllByOrderByPaceAsc();

    List<ExerciseModel> findAllByOrderByDistanceInKmDesc();
}

