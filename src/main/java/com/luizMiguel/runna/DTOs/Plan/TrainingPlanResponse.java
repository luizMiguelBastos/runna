package com.luizMiguel.runna.DTOs.Plan;

import java.util.List;

public record TrainingPlanResponse(
        String goal,
        List<PlanWeek> weeks
) {
}
