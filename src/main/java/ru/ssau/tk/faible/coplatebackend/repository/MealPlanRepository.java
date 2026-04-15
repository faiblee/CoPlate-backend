package ru.ssau.tk.faible.coplatebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.faible.coplatebackend.entity.Family;
import ru.ssau.tk.faible.coplatebackend.entity.MealPlan;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    List<MealPlan> findByFamilyIsAndDayOfWeekEqualsAndMealTypeEquals(Family family, Integer dayOfWeek, String mealType);
    List<MealPlan> findByFamilyIsAndDayOfWeekEquals(Family family, Integer dayOfWeek);
    List<MealPlan> findByFamilyId(Long id);
    void deleteAllByFamilyId(Long id);
}
