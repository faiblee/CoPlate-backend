package ru.ssau.tk.faible.coplatebackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "dishes")
@NoArgsConstructor
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @ColumnDefault("'library'")
    @Column(name = "source", nullable = false, length = 10)
    private String source = "library";

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "family_id")
    private Family family;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "dish", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<DishIngredient> ingredients = new LinkedList<>();

    public Dish(String name, String description, String source, Family family, User createdBy) {
        this.name = name;
        this.description = description;
        this.source = source;
        this.family = family;
        this.createdBy = createdBy;
    }
}