package ru.ssau.tk.faible.coplatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ssau.tk.faible.coplatebackend.entity.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyRequest {
    private String name;
    private Long ownerId;
}
