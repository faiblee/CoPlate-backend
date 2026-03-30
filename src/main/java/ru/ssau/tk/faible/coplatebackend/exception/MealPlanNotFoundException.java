package ru.ssau.tk.faible.coplatebackend.exception;

public class MealPlanNotFoundException extends RuntimeException {
    public MealPlanNotFoundException() {
        super("Meal plan not found, backend error");
    }
}
