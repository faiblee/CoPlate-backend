package ru.ssau.tk.faible.coplatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ssau.tk.faible.coplatebackend.entity.Dish;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishResponse {
    private Long id;
    private String name;
    private String description;
    private String source;
    private Long familyId;
    private Long ownerId;

    // Для "библиотечных" блюд

    public DishResponse(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public DishResponse(Dish dish) {
        this.id = dish.getId();
        this.name = dish.getName();
        this.description = dish.getDescription();
        this.source = dish.getSource();
        if (Objects.equals(this.source, "library")) {
            this.ownerId = null;
            this.familyId = null;
        } else {
            this.ownerId = dish.getCreatedBy().getId();
            this.familyId = dish.getFamily().getId();
        }
    }


}
