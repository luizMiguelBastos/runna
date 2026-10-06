package com.luizMiguel.runna.DTOs.Plan;

import java.util.List;

public record PlanWeek(
        Integer week,
        List<PlannedSession> sessions
) {
}
