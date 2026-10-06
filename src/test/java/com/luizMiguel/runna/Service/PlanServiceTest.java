package com.luizMiguel.runna.Service;

import com.luizMiguel.runna.DTOs.Exercise.CreatePlanRequest;
import com.luizMiguel.runna.DTOs.Plan.PlanWeek;
import com.luizMiguel.runna.DTOs.Plan.PlannedSession;
import com.luizMiguel.runna.DTOs.Plan.TrainingPlanResponse;
import com.luizMiguel.runna.Services.PlanService;
import com.luizMiguel.runna.Services.PlanService.UserLevel;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.IntStream;

import static com.luizMiguel.runna.Models.ExerciseModel.ExerciseType.BIKE;
import static com.luizMiguel.runna.Models.ExerciseModel.ExerciseType.RUN;
import static com.luizMiguel.runna.Models.ExerciseModel.ExerciseType.WALK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private static TrainingPlanResponse planWithWeeks(int count) {
        return new TrainingPlanResponse("qualquer", IntStream.rangeClosed(1, count)
                .mapToObj(i -> new PlanWeek(i, List.of(run(3.0))))
                .toList());
    }

    private static Deque<TrainingPlanResponse> responses(TrainingPlanResponse... plans) {
        return new ArrayDeque<>(List.of(plans));
    }

    @Test
    void deveMontarGoalComDistanciaInteiraSemCasaDecimal() {
        assertEquals("Correr 5 km em 6 semanas",
                PlanService.buildGoal(new CreatePlanRequest(RUN, 5.0, 6)));
    }

    @Test
    void deveMontarGoalComVerboDeCadaTipoEDistanciaDecimal() {
        assertEquals("Caminhar 7,5 km em 8 semanas",
                PlanService.buildGoal(new CreatePlanRequest(WALK, 7.5, 8)));
        assertEquals("Pedalar 21,1 km em 1 semana",
                PlanService.buildGoal(new CreatePlanRequest(BIKE, 21.1, 1)));
    }

    @Test
    void deveValidarQuantidadeDeSemanas() {
        assertTrue(PlanService.hasExpectedWeeks(planWithWeeks(6), 6));
        assertFalse(PlanService.hasExpectedWeeks(planWithWeeks(4), 6));
        assertFalse(PlanService.hasExpectedWeeks(new TrainingPlanResponse("qualquer", null), 6));
        assertFalse(PlanService.hasExpectedWeeks(null, 6));
    }

    @Test
    void naoDeveChamarIaDeNovoQuandoPlanoVemCerto() {
        Deque<TrainingPlanResponse> calls = responses(planWithWeeks(6));
        TrainingPlanResponse correct = calls.peek();

        TrainingPlanResponse result = PlanService.fetchPlanWithExpectedWeeks(calls::pop, 6);

        assertSame(correct, result);
        assertTrue(calls.isEmpty());
    }

    @Test
    void deveTentarDeNovoUmaVezQuandoQuantidadeDeSemanasVemErrada() {
        TrainingPlanResponse correct = planWithWeeks(6);
        Deque<TrainingPlanResponse> calls = responses(planWithWeeks(4), correct);

        TrainingPlanResponse result = PlanService.fetchPlanWithExpectedWeeks(calls::pop, 6);

        assertSame(correct, result);
    }

    @Test
    void deveLancarErroQuandoIaErraDuasVezes() {
        Deque<TrainingPlanResponse> calls = responses(planWithWeeks(4), planWithWeeks(5), planWithWeeks(6));

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> PlanService.fetchPlanWithExpectedWeeks(calls::pop, 6));

        assertEquals(HttpStatus.BAD_GATEWAY, e.getStatusCode());
        assertTrue(e.getReason().contains("5 semanas em vez de 6"));
        assertEquals(1, calls.size());
    }
}
