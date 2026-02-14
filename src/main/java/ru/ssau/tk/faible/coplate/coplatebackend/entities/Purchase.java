package ru.ssau.tk.faible.coplate.coplatebackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @ColumnDefault("false")
    @Column(name = "is_bought", nullable = false)
    private Boolean isBought = false;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ColumnDefault("'шт'")
    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "source", nullable = false, length = 10)
    private String source;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "dish_id")
    private Dish dish;

}