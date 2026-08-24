package com.luizMiguel.runna.Services;

import com.luizMiguel.runna.Repositorys.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }




}
