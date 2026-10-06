package com.luizMiguel.runna.Service;

import com.luizMiguel.runna.DTOs.Auth.LoginResponse;
import com.luizMiguel.runna.Models.UserModel;
import com.luizMiguel.runna.Security.JwtService;
import com.luizMiguel.runna.Services.AuthService;
import com.luizMiguel.runna.Services.UserService;
import io.jsonwebtoken.security.Password;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private AuthService authService;

    @Test
    void deveRetornarTokenQuandoLoginValido (){
        UserModel user = new UserModel();
        String username = "miguel123";
        String password = "12345";
        String token = "tokenFalso";
        when(userService.userValidation("miguel123", "12345")).thenReturn(user);
        when(jwtService.createToken(user)).thenReturn(token);
        LoginResponse loginResponse = authService.login("miguel123", "12345");
        assertEquals(token, loginResponse.token());
    }


}
