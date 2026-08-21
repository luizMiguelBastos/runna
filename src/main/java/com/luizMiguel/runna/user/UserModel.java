package com.luizMiguel.runna.user;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


import java.util.UUID;

@Entity
@Table(name = "runna_users")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class UserModel {

    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
}
