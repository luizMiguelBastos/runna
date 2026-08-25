package com.luizMiguel.runna.DTOs.User;

import java.util.UUID;

public record CreateUserRequest(UUID id, String username) {
}
