package ru.ssau.tk.faible.coplatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishCreateCustomRequest {
    private String name;
    private String description;
    private String source = "library";
    private Long familyId;
    private Long ownerId;

    // для "библиотечных" блюд
    public DishCreateCustomRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
