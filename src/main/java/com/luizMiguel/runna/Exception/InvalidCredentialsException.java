package com.luizMiguel.runna.Exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Wrong credentials!");
    }
}
