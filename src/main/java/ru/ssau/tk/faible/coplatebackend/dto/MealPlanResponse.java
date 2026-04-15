package ru.ssau.tk.faible.coplatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ssau.tk.faible.coplatebackend.entity.Dish;
import ru.ssau.tk.faible.coplatebackend.entity.MealPlan;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealPlanResponse {
    private List<Long> ids;
    private Long familyId;
    private List<Long> dishIds;
    private Integer dayOfWeek;
    private String mealType;

    public MealPlanResponse(List<MealPlan> mealPlans) {
        if (!mealPlans.isEmpty()) {
            this.ids = mealPlans.stream().map(MealPlan::getId).toList();
            this.familyId = mealPlans.get(0).getFamily().getId();
            this.dishIds = mealPlans.stream().map(MealPlan::getDish).filter(Objects::nonNull).map(Dish::getId).toList();
            this.dayOfWeek = mealPlans.get(0).getDayOfWeek();
            this.mealType = mealPlans.get(0).getMealType();
        }
    }
}
