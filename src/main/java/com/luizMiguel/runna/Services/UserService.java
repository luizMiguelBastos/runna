package com.luizMiguel.runna.Services;

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

 public UserModel createUser (String username, String password){
     UserModel user = new UserModel();

     user.setUsername(username);
     String passwordHash = passwordEncoder.encode(password);
     user.setPassword(passwordHash);

     return userRepository.save(user);
 }

    public UserModel userLogin(String username, String password) {

        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Wrong credentials!")
                );
        boolean senhaCorreta = passwordEncoder.matches(
                password,
                user.getPassword()
        );
        if (!senhaCorreta) {
            throw new RuntimeException("Wrong credentials!");
        }
        return user;
    }


}
