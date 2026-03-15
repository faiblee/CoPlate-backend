package ru.ssau.tk.faible.coplatebackend.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
    public UnauthorizedException() {
        super("Unauthorized, access denies");
    }
}
