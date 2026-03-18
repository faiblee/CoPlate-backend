package ru.ssau.tk.faible.coplatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ssau.tk.faible.coplatebackend.entity.Family;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyResponse {
    private Long id;
    private String name;
    private Long ownerId;

    public FamilyResponse(Family family) {
        this.id = family.getId();
        this.name = family.getName();
        this.ownerId = family.getOwner().getId();
    }
}
