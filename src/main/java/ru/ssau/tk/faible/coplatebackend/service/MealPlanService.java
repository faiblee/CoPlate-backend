package ru.ssau.tk.faible.coplatebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ssau.tk.faible.coplatebackend.dto.*;
import ru.ssau.tk.faible.coplatebackend.entity.*;
import ru.ssau.tk.faible.coplatebackend.exception.*;
import ru.ssau.tk.faible.coplatebackend.repository.DishRepository;
import ru.ssau.tk.faible.coplatebackend.repository.FamilyRepository;
import ru.ssau.tk.faible.coplatebackend.repository.MealPlanRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final FamilyRepository familyRepository;
    private final DishRepository dishRepository;

    public MealPlanResponse addDishToMealPlan(MealPlanAddRequest request, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        Family family = familyRepository.findById(request.getFamilyId())
                .orElseThrow(() -> new FamilyNotFoundException(request.getFamilyId()));

        List<Long> familyMembersIds = family.getUsers().stream().map(User::getId).toList();

        if (!familyMembersIds.contains(currentUser.getId()) && !Objects.equals(currentUser.getRole(), "admin")) {
           throw new ForbiddenException();
        }

        Dish dish = dishRepository.findById(request.getDishId())
                .orElseThrow(() -> new DishNotFoundException(request.getDishId()));

        if (!(Objects.equals(dish.getSource(), "library") || dish.getFamily().getId().equals(family.getId()) || currentUser.getRole().equals("admin"))) {
            throw new ForbiddenException();
        }

        MealPlan mealPlan = mealPlanRepository.findByFamilyIsAndDayOfWeekEqualsAndMealTypeEquals(family, request.getDayOfWeek(), request.getMealType())
                .orElseThrow(MealPlanNotFoundException::new);

        mealPlan.setDish(dish);

        MealPlan savedMealPlan = mealPlanRepository.save(mealPlan);

        return new MealPlanResponse(savedMealPlan);
    }

    public void clearWeekPlan(Long familyId, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException(familyId));

        List<Long> familyMembersIds = family.getUsers().stream().map(User::getId).toList();

        if (!familyMembersIds.contains(currentUser.getId()) && !Objects.equals(currentUser.getRole(), "admin")) {
            throw new ForbiddenException();
        }

        for (int i = 1; i <= 7; i++) {
            List<MealPlan> mealPlans = mealPlanRepository.findByFamilyIsAndDayOfWeekEquals(family, i);
            for (MealPlan plan : mealPlans) {
                plan.setDish(null);
                mealPlanRepository.save(plan);
            }
        }
    }

    public WeekPlanResponse getWeekFamilyMealPlan(Long familyId, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException(familyId));

        List<Long> familyMembersIds = family.getUsers().stream().map(User::getId).toList();

        if (!familyMembersIds.contains(currentUser.getId()) && !Objects.equals(currentUser.getRole(), "admin")) {
            throw new ForbiddenException();
        }

        List<MealPlan> plans = mealPlanRepository.findByFamilyId(familyId);

        List<DayMenu> days = new ArrayList<>();
        for (int day = 1; day <= 7; day++) {
            DayMenu dayMenu = new DayMenu(
                    day,
                    findMealSlot(plans, day, "breakfast"),
                    findMealSlot(plans, day, "lunch"),
                    findMealSlot(plans, day, "dinner")
            );
            days.add(dayMenu);
        }

        return new WeekPlanResponse(familyId, days);
    }

    private MealSlot findMealSlot(List<MealPlan> plans, int dayOfWeek, String mealType) {
        return plans.stream()
                .filter(p -> p.getDayOfWeek() == dayOfWeek && p.getMealType().equals(mealType))
                .findFirst()
                .map(plan -> new MealSlot(
                        mealType,
                        plan.getDish() != null ? new DishInfoResponse(
                                plan.getDish().getId(),
                                plan.getDish().getName(),
                                plan.getDish().getDescription(),
                                plan.getDish().getSource()
                        ) : null
                ))
                .orElse(new MealSlot(mealType, null));
    }
}
