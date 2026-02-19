package ru.ssau.tk.faible.coplatebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.faible.coplatebackend.entity.Dish;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

}
