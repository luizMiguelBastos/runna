package com.luizMiguel.runna.Controllers;

import com.luizMiguel.runna.DTOs.Exercise.CreateExerciseRequest;
import com.luizMiguel.runna.DTOs.Exercise.ExerciseResponse;
import com.luizMiguel.runna.Models.UserModel;
import com.luizMiguel.runna.Services.ExerciseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.rmi.server.UID;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/exercises")
public class ExerciseController {

    public final ExerciseService exerciseService;


    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping
    public ExerciseResponse create (@RequestBody CreateExerciseRequest createExercise, @AuthenticationPrincipal UUID userID){
        return exerciseService.createExercise(userID, createExercise);
    }

    @GetMapping
    public List<ExerciseResponse> list (@AuthenticationPrincipal UUID userId, @RequestParam(defaultValue = "NEWEST") ExerciseService.Ordenacao ordem){

        return exerciseService.listExercises(userId, ordem);
    }

}
