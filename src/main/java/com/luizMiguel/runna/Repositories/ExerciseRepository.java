package com.luizMiguel.runna.Repositories;

import com.luizMiguel.runna.Models.ExerciseModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExerciseRepository extends JpaRepository<ExerciseModel, UUID> {

    List<ExerciseModel> findAllByUser_IdOrderByCreatedAtDesc(UUID userId);

    List<ExerciseModel> findAllByUser_IdOrderByCreatedAtAsc(UUID userId);

    List<ExerciseModel> findAllByUser_IdOrderByPaceAsc(UUID userId);

    List<ExerciseModel> findAllByUser_IdOrderByDistanceInKmDesc(UUID userId);

    Optional<ExerciseModel> findByIdAndUser_Id(UUID id, UUID userId);

    void deleteByIdAndUser_Id(UUID id, UUID userId);

}

