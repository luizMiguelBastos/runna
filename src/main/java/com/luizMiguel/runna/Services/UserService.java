package com.luizMiguel.runna.Services;

import com.luizMiguel.runna.DTOs.User.UserResponse;
import com.luizMiguel.runna.Exception.InvalidCredentialsException;
import com.luizMiguel.runna.Models.UserModel;
import com.luizMiguel.runna.Repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

 public UserResponse createUser (String username, String password){
     UserModel user = new UserModel();

     user.setUsername(username);
     String passwordHash = passwordEncoder.encode(password);
     user.setPassword(passwordHash);
     UserModel savedUser = userRepository.save(user);
     return new UserResponse (savedUser.getId(), savedUser.getUsername());

 }

    public UserModel userLogin(String username, String password) {

        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new InvalidCredentialsException()
                );
        boolean senhaCorreta = passwordEncoder.matches(
                password,
                user.getPassword()
        );
        if (!senhaCorreta) {
            throw new InvalidCredentialsException();
        }
        return user;
    }


}
