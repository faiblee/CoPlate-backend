package ru.ssau.tk.faible.coplatebackend.exception;

public class FamilyNotFoundException extends RuntimeException {
    public FamilyNotFoundException() {
        super("Family not found");
    }

    public FamilyNotFoundException(Long id) {
        super("Family with id = " + id + "not found");
    }
}
