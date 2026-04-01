package ru.ssau.tk.faible.coplatebackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DishCreateCustomRequest {
    private String name;
    private String description;
    private String source = "library";
    private Long familyId;
    private Long ownerId;
    private List<DishIngredientRequest> ingredients;

    // для "библиотечных" блюд
    public DishCreateCustomRequest(String name, String description, List<DishIngredientRequest> ingredients) {
        this.name = name;
        this.description = description;
        this.ingredients.addAll(ingredients);
    }

    public DishCreateCustomRequest(String name, String description, String source, Long familyId, Long ownerId, List<DishIngredientRequest> ingredients) {
        this.name = name;
        this.description = description;
        this.source = source;
        this.familyId = familyId;
        this.ownerId = ownerId;
        this.ingredients.addAll(ingredients);
    }
}
