package com.luizMiguel.runna.Controllers;

import com.luizMiguel.runna.DTOs.User.CreateUserRequest;
import com.luizMiguel.runna.DTOs.User.UserResponse;
import com.luizMiguel.runna.Models.UserModel;
import com.luizMiguel.runna.Services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public UserResponse create (@RequestBody CreateUserRequest user){
        return userService.createUser(user.username(), user.password());
    }
}
