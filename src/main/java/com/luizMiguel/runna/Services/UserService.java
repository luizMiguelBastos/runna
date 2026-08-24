package com.luizMiguel.runna.Services;

import com.luizMiguel.runna.Models.UserModel;
import com.luizMiguel.runna.Repositorys.UserRepository;
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

 public UserModel createUser (String username, String password){
     UserModel user = new UserModel();

     user.setUsername(username);
     user.setPassword(passwordEncoder.encode(password));

     return userRepository.save(user);
 }

    public UserModel userLogin(String username, String password) {

        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        return user;
    }



}
