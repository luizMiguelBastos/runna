package com.luizMiguel.runna.Services;

import com.luizMiguel.runna.DTOs.Exercise.CreatePlanRequest;
import com.luizMiguel.runna.DTOs.Plan.TrainingPlanResponse;
import com.luizMiguel.runna.Models.ExerciseModel;
import com.luizMiguel.runna.Repositories.ExerciseRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PlanService {

    private static final String SYSTEM_PROMPT = """
            Você é um treinador de corrida, caminhada e ciclismo.
            Monte planos de treino progressivos e seguros, em português.
            Nunca aumente a distância do treino mais longo em mais de 10% de uma semana para a outra.
            Use apenas os tipos de exercício RUN, WALK ou BIKE.
            """;

    private final ExerciseRepository exerciseRepository;
    private final ChatClient chatClient;

    public PlanService(ExerciseRepository exerciseRepository, ChatClient.Builder chatClientBuilder) {
        this.exerciseRepository = exerciseRepository;
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

    public record UserLevel(int sessions, double sessionsPerWeek, double maxDistanceKm, long avgPaceSeconds) {

        public static final UserLevel BEGINNER = new UserLevel(0, 0, 0, 0);

        public boolean isBeginner() {
            return sessions == 0;
        }
    }

    private UserLevel calculateLevel(UUID userId, ExerciseModel.ExerciseType type) {
        Instant since = Instant.now().minus(Duration.ofDays(28));

        List<ExerciseModel> recent = exerciseRepository.findAllByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(e -> e.getType() == type)
                .filter(e -> e.getCreatedAt().isAfter(since))
                .toList();

        if (recent.isEmpty()) {
            return UserLevel.BEGINNER;
        }

        double maxDistance = recent.stream()
                .mapToDouble(ExerciseModel::getDistanceInKm)
                .max()
                .orElse(0);

        long avgPaceSeconds = Math.round(recent.stream()
                .mapToLong(e -> e.getPace().toSeconds())
                .average()
                .orElse(0));

        return new UserLevel(recent.size(), recent.size() / 4.0, maxDistance, avgPaceSeconds);
    }

    private String levelToPrompt(UserLevel level) {
        if (level.isBeginner()) {
            return "Usuário iniciante, sem treinos desse tipo nas últimas 4 semanas.";
        }

        return "Últimas 4 semanas: %d treinos (%.1f por semana), maior distância %.1f km, pace médio %d:%02d/km."
                .formatted(level.sessions(), level.sessionsPerWeek(), level.maxDistanceKm(),
                        level.avgPaceSeconds() / 60, level.avgPaceSeconds() % 60);
    }

    public TrainingPlanResponse generatePlan(UUID userId, CreatePlanRequest request) {
        UserLevel userLevel = calculateLevel(userId, request.type());
        String level = levelToPrompt(userLevel);

        return chatClient.prompt()
                .user(u -> u.text("""
                    Meta: {type}, conseguir fazer {distance} km em um único treino.
                    Prazo: {weeks} semanas.
                    Nível atual: {level}
                    Monte um plano com exatamente {weeks} semanas, de 2 a 4 treinos por semana.
                    """)
                        .param("type", request.type().name())
                        .param("distance", request.targetDistanceKm())
                        .param("weeks", request.weeks())
                        .param("level", level))
                .call()
                .entity(TrainingPlanResponse.class);
    }

}
