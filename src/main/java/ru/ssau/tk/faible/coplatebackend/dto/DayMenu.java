package ru.ssau.tk.faible.coplatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DayMenu {
    private Integer dayOfWeek;
    private MealSlot breakfast;
    private MealSlot lunch;
    private MealSlot dinner;
}
