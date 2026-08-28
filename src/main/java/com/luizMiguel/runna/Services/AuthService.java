package com.luizMiguel.runna.Services;

import com.luizMiguel.runna.DTOs.Auth.LoginResponse;
import com.luizMiguel.runna.Models.UserModel;
import com.luizMiguel.runna.Security.JwtService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;


    public AuthService(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public LoginResponse login(String username, String password){
        UserModel user = userService.userLogin(username, password);
        String token = jwtService.createToken(user);

        return new LoginResponse(token);
    }
}
