package ru.ssau.tk.faible.coplatebackend.exception;

public class BadCreateDishRequest extends RuntimeException {
    public BadCreateDishRequest(String message) {
        super(message);
    }

    public BadCreateDishRequest() {
        super("Bad create dish request (incongruity 'source', ids and request body)");
    }
}
