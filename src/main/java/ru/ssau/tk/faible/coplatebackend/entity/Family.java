package ru.ssau.tk.faible.coplatebackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "families")
@NoArgsConstructor
public class Family {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "invite_code", length = 10, unique = true)
    private String inviteCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "family", fetch = FetchType.LAZY)
    List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "family", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Dish> dishes = new LinkedList<>();

    @OneToMany(mappedBy = "family", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Purchase> purchases = new LinkedList<>();

    @OneToMany(mappedBy = "family", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<MealPlan> mealPlans = new LinkedList<>();

    public Family(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }
    public Family(String name, User owner, String inviteCode) {
        this.name = name;
        this.owner = owner;
        this.inviteCode = inviteCode;
    }
}