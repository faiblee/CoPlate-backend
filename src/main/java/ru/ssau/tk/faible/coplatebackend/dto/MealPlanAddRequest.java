package ru.ssau.tk.faible.coplatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealPlanAddRequest {
    private Long familyId;
    private Long dishId;
    private Integer dayOfWeek;
    private String mealType;
}
