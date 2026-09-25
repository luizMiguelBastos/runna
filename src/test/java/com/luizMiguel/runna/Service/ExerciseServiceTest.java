package com.luizMiguel.runna.Service;

import com.luizMiguel.runna.DTOs.Exercise.CreateExerciseRequest;
import com.luizMiguel.runna.DTOs.Exercise.ExerciseResponse;
import com.luizMiguel.runna.Models.ExerciseModel;
import com.luizMiguel.runna.Models.UserModel;
import com.luizMiguel.runna.Repositories.ExerciseRepository;
import com.luizMiguel.runna.Repositories.UserRepository;
import com.luizMiguel.runna.Services.ExerciseService;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    @Test
    void deveCriarExercicioComPaceCalculado() {
        UUID userId = UUID.randomUUID();
        CreateExerciseRequest request = new CreateExerciseRequest(
                ExerciseModel.ExerciseType.RUN,
                Duration.ofMinutes(30),
                5.0
        );

        when(userRepository.getReferenceById(userId)).thenReturn(new UserModel());
        when(exerciseRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));

        ExerciseResponse response = exerciseService.createExercise(userId, request);

        assertEquals(Duration.ofMinutes(6), response.pace());
    }

    @Test
    void deveLancarErroQuandoExercicioNaoExiste(){
        UUID userId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();

        when(exerciseRepository.findByIdAndUser_Id(exerciseId,userId)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> exerciseService.search(userId,exerciseId));
    }


    @Test
    void deveDeletarExercicioDoUsuario(){
        ExerciseModel exerciseModel = new ExerciseModel();
        UUID userId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();

        when(exerciseRepository.findByIdAndUser_Id(exerciseId, userId)).thenReturn(Optional.of(exerciseModel));
        exerciseService.delete(userId, exerciseId);
        verify(exerciseRepository).delete(exerciseModel);



    }
}
