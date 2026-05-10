package ru.ssau.tk.faible.coplatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DishInfoResponseWithMealPlan {
    private Long id;
    private Long planId;
    private String name;
    private String description;
    private String source;
}
