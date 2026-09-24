package com.luizMiguel.runna.User;

import com.luizMiguel.runna.Exception.InvalidCredentialsException;
import com.luizMiguel.runna.Models.UserModel;
import com.luizMiguel.runna.Repositories.UserRepository;
import com.luizMiguel.runna.Services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void usernameInexistente(){
        String username = "miguel";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(InvalidCredentialsException.class, () -> userService.userLogin(username, null));
    }

    @Test
    void usuarioExisteMasSenhaErrada(){
        UserModel user = new UserModel();
        String username = "miguel";
        String password = "miguel123";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);
        assertThrows(InvalidCredentialsException.class, () -> userService.userLogin(username,password));
    }

    @Test
    void deveRetornarUsuarioQuandoCredenciaisCorretas(){
        UserModel user = new UserModel();
        String username = "miguel";
        String password = "miguel123";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        assertEquals(user, userService.userLogin(username, password));
    }


}
