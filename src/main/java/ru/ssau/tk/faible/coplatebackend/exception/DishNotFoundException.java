package ru.ssau.tk.faible.coplatebackend.exception;

public class DishNotFoundException extends RuntimeException {
    public DishNotFoundException(Long id) {
        super("Dish with ud = " + id + "not found");
    }
}
