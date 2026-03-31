package ru.ssau.tk.faible.coplatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ssau.tk.faible.coplatebackend.entity.Purchase;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseResponse {
    Long id;
    private String name;
    private Integer quantity;
    private String unit;
    private Boolean isBought;

    public PurchaseResponse(Purchase purchase) {
        this.id = purchase.getId();
        this.name = purchase.getName();
        this.quantity = purchase.getQuantity();
        this.unit = purchase.getUnit();
        this.isBought = purchase.getIsBought();
    }
}
