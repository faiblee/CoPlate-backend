package ru.ssau.tk.faible.coplatebackend.exception;

public class PurchaseNotFoundException extends RuntimeException {
    public PurchaseNotFoundException() {
        super("Purchase not found");
    }

    public PurchaseNotFoundException(Long id) {
        super("Purchase with id = " + id + "not found");
    }
}
