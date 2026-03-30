package ru.ssau.tk.faible.coplatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ssau.tk.faible.coplatebackend.entity.MealPlan;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealPlanResponse {
    private Long id;
    private Long familyId;
    private Long dishId;
    private Integer dayOfWeek;
    private String mealType;

    public MealPlanResponse(MealPlan mealPlan) {
        this.id = mealPlan.getId();
        this.familyId = mealPlan.getFamily().getId();
        this.dishId = mealPlan.getDish().getId();
        this.dayOfWeek = mealPlan.getDayOfWeek();
        this.mealType = mealPlan.getMealType();
    }
}
