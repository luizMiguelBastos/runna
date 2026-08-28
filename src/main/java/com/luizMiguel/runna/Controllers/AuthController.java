package com.luizMiguel.runna.Controllers;

import com.luizMiguel.runna.DTOs.Auth.LoginRequest;
import com.luizMiguel.runna.DTOs.Auth.LoginResponse;
import com.luizMiguel.runna.Services.AuthService;
import com.luizMiguel.runna.Services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest login) {

        return authService.login(login.username(), login.password());
    }
}
