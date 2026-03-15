package ru.ssau.tk.faible.coplatebackend.exception;

public class AlreadyFamilyException extends RuntimeException {
    public AlreadyFamilyException(String message) {
        super(message);
    }
    public AlreadyFamilyException(Long id) {
        super("User with id=" + id + "is already in the family");
    }
}
