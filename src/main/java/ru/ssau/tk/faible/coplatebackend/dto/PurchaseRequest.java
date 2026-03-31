package ru.ssau.tk.faible.coplatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequest {
    private String name;
    private Integer quantity;
    private String unit;
    private String source;
    private Long dishId;
}
