package com.luizMiguel.runna.Controllers;

import com.luizMiguel.runna.DTOs.Exercise.CreateExerciseRequest;
import com.luizMiguel.runna.DTOs.Exercise.CreatePlanRequest;
import com.luizMiguel.runna.DTOs.Exercise.ExerciseResponse;
import com.luizMiguel.runna.DTOs.Plan.TrainingPlanResponse;
import com.luizMiguel.runna.Services.ExerciseService;
import com.luizMiguel.runna.Services.PlanService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;
    private final PlanService planService;


    public ExerciseController(ExerciseService exerciseService, PlanService planService) {
        this.exerciseService = exerciseService;
        this.planService = planService;
    }

    @PostMapping("/plan")
    public TrainingPlanResponse plan(@AuthenticationPrincipal UUID userId,
                                     @Valid @RequestBody CreatePlanRequest request) {
        return planService.generatePlan(userId, request);
    }

    @PostMapping
    public ExerciseResponse create (@RequestBody CreateExerciseRequest createExercise, @AuthenticationPrincipal UUID userID){
        return exerciseService.createExercise(userID, createExercise);
    }

    @GetMapping
    public List<ExerciseResponse> list (@AuthenticationPrincipal UUID userId, @RequestParam(defaultValue = "NEWEST") ExerciseService.OrderBy order){
        return exerciseService.listExercises(userId, order);
    }

    @GetMapping("/{id}")
    public ExerciseResponse search (@AuthenticationPrincipal UUID userId, @PathVariable UUID id){
        return exerciseService.search(userId, id);
    }

    @DeleteMapping("/{id}")
    public void delete (@AuthenticationPrincipal UUID userId, @PathVariable UUID id){
        exerciseService.delete(userId, id);
    }



}
