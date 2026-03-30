package ru.ssau.tk.faible.coplatebackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "meal_plans")
@AllArgsConstructor
@NoArgsConstructor
public class MealPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "family_id")
    private Family family;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "dish_id")
    private Dish dish;

    @Column(name = "day_of_week", nullable = false) // 1-monday, 2-tuesday, ..., 7-sunday
    private Integer dayOfWeek;

    @Column(name = "meal_type", nullable = false, length = 20) // breakfast, lunch, dinner
    private String mealType;

    public MealPlan(Family family, Dish dish, Integer dayOfWeek, String mealType) {
        this.family = family;
        this.dish = dish;
        this.dayOfWeek = dayOfWeek;
        this.mealType = mealType;
    }
}