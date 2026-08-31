package com.luizMiguel.runna.Services;

import com.luizMiguel.runna.DTOs.Exercise.CreateExerciseRequest;
import com.luizMiguel.runna.DTOs.Exercise.ExerciseResponse;
import com.luizMiguel.runna.Models.ExerciseModel;
import com.luizMiguel.runna.Models.UserModel;
import com.luizMiguel.runna.Repositories.ExcerciseRepository;
import com.luizMiguel.runna.Repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ExerciseService {

    public enum OrderBy { NEWEST, OLDEST, BEST_PACE, LONGEST_DISTANCE}

    private final ExcerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    public ExerciseService(ExcerciseRepository exerciseRepository,
                           UserRepository userRepository) {
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ExerciseResponse createExercise(UUID userId, CreateExerciseRequest request) {
        UserModel user = userRepository.getReferenceById(userId);

        ExerciseModel exercise = new ExerciseModel();
        exercise.setUser(user);
        exercise.setType(request.type());
        exercise.setDuration(request.duration());
        exercise.setDistanceInKm(request.distanceInKm());
        exercise.paceCalc();

        return toResponse(exerciseRepository.saveAndFlush(exercise));
    }

    @Transactional(readOnly = true)
    public List<ExerciseResponse> listExercises(UUID userId, OrderBy order) {
        List<ExerciseModel> exercises = switch (order) {
            case NEWEST        -> exerciseRepository.findAllByUser_IdOrderByCreatedAtDesc(userId);
            case OLDEST         -> exerciseRepository.findAllByUser_IdOrderByCreatedAtAsc(userId);
            case BEST_PACE     -> exerciseRepository.findAllByUser_IdOrderByPaceAsc(userId);
            case LONGEST_DISTANCE -> exerciseRepository.findAllByUser_IdOrderByDistanceInKmDesc(userId);
        };

        return exercises.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ExerciseResponse search(UUID userId, UUID exerciseId) {
        return exerciseRepository.findByIdAndUser_Id(exerciseId, userId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Exercise not found"));
    }

    @Transactional
    public void delete(UUID userId, UUID exerciseId) {
        ExerciseModel exercise = exerciseRepository.findByIdAndUser_Id(exerciseId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Exercise not found"));;

        exerciseRepository.delete(exercise);
    }

    private ExerciseResponse toResponse(ExerciseModel e) {
        return new ExerciseResponse(
                e.getId(),
                e.getType(),
                e.getDuration(),
                e.getDistanceInKm(),
                e.getPace(),
                e.getCreatedAt()
        );
    }
}

