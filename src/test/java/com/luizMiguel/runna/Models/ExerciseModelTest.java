package com.luizMiguel.runna.Models;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExerciseModelTest {

    @Test
    void deveCalcularPace(){
        ExerciseModel model = new ExerciseModel();
        model.setDuration(Duration.ofMinutes(30));
        model.setDistanceInKm(5.0);
        model.paceCalc();
        assertEquals(Duration.ofMinutes(6), model.getPace());
    }

    @Test
    void DeveLancarErroDaDistancia(){
        ExerciseModel model = new ExerciseModel();
        model.setDuration(Duration.ofMinutes(30));
        model.setDistanceInKm(0);
        assertThrows(IllegalArgumentException.class, () -> model.paceCalc());
    }
}
