package ru.ssau.tk.faible.coplatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DishInfoResponse {
    private Long id;
    private String name;
    private String description;
    private String source;
}
