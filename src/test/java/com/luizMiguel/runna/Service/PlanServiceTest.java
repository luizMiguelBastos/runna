package com.luizMiguel.runna.Service;

import com.luizMiguel.runna.DTOs.Plan.PlanWeek;
import com.luizMiguel.runna.DTOs.Plan.PlannedSession;
import com.luizMiguel.runna.DTOs.Plan.TrainingPlanResponse;
import com.luizMiguel.runna.Services.PlanService;
import com.luizMiguel.runna.Services.PlanService.UserLevel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.luizMiguel.runna.Models.ExerciseModel.ExerciseType.RUN;
import static com.luizMiguel.runna.Models.ExerciseModel.ExerciseType.WALK;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlanServiceTest {

    private static final UserLevel LEVEL_4KM = new UserLevel(3, 0.75, 4.0, 375);

    private static PlannedSession run(double km) {
        return new PlannedSession(RUN, km, "6:30", "Treino");
    }

    private static TrainingPlanResponse plan(PlanWeek... weeks) {
        return new TrainingPlanResponse("Correr 5 km em 4 semanas", List.of(weeks));
    }

    private static List<Double> distances(TrainingPlanResponse plan, int weekIndex) {
        return plan.weeks().get(weekIndex).sessions().stream()
                .map(PlannedSession::targetDistanceKm)
                .toList();
    }

    @Test
    void deveCortarSemanaQuePassaDoLimite() {
        TrainingPlanResponse result = PlanService.enforceWeeklyProgression(plan(
                new PlanWeek(1, List.of(run(3.0), run(5.0))),
                new PlanWeek(2, List.of(run(5.0)))
        ), LEVEL_4KM);

        assertEquals(List.of(3.0, 4.4), distances(result, 0));
        assertEquals(List.of(4.8), distances(result, 1));
    }

    @Test
    void naoDeveAlterarSemanaDentroDoLimite() {
        TrainingPlanResponse result = PlanService.enforceWeeklyProgression(plan(
                new PlanWeek(1, List.of(run(3.0), run(4.4))),
                new PlanWeek(2, List.of(run(4.8))),
                new PlanWeek(3, List.of(run(4.0)))
        ), LEVEL_4KM);

        assertEquals(List.of(3.0, 4.4), distances(result, 0));
        assertEquals(List.of(4.8), distances(result, 1));
        assertEquals(List.of(4.0), distances(result, 2));
    }

    @Test
    void inicianteNaoDeveLimitarSemanaUm() {
        TrainingPlanResponse result = PlanService.enforceWeeklyProgression(plan(
                new PlanWeek(1, List.of(run(10.0))),
                new PlanWeek(2, List.of(run(12.0)))
        ), UserLevel.BEGINNER);

        assertEquals(List.of(10.0), distances(result, 0));
        assertEquals(List.of(11.0), distances(result, 1));
    }

    @Test
    void deveRemoverSessaoComZeroKm() {
        TrainingPlanResponse result = PlanService.enforceWeeklyProgression(plan(
                new PlanWeek(1, List.of(
                        run(4.0),
                        new PlannedSession(WALK, 0.0, "", "Treino de força"),
                        new PlannedSession(WALK, null, "", "Sem distância")
                ))
        ), LEVEL_4KM);

        assertEquals(List.of(4.0), distances(result, 0));
    }
}
