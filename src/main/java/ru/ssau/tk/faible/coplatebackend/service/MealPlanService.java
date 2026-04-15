package ru.ssau.tk.faible.coplatebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.faible.coplatebackend.dto.*;
import ru.ssau.tk.faible.coplatebackend.entity.*;
import ru.ssau.tk.faible.coplatebackend.exception.*;
import ru.ssau.tk.faible.coplatebackend.repository.DishRepository;
import ru.ssau.tk.faible.coplatebackend.repository.FamilyRepository;
import ru.ssau.tk.faible.coplatebackend.repository.MealPlanRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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


        MealPlan plan = new MealPlan(family, dish, request.getDayOfWeek(), request.getMealType());
        mealPlanRepository.save(plan);

        List<MealPlan> mealPlansAfterSave = mealPlanRepository.findByFamilyIsAndDayOfWeekEqualsAndMealTypeEquals(family, request.getDayOfWeek(), request.getMealType());

        return new MealPlanResponse(mealPlansAfterSave);
    }

    @Transactional
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

        mealPlanRepository.deleteAllByFamilyId(familyId);
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
            MealSlot breakfast = findMealSlot(plans, day, "breakfast");
            MealSlot lunch = findMealSlot(plans, day, "lunch");
            MealSlot dinner = findMealSlot(plans, day, "dinner");
            DayMenu dayMenu = new DayMenu();
            dayMenu.setDayOfWeek(day);
            if (!breakfast.getDishes().isEmpty()) {
                dayMenu.setBreakfast(breakfast);
            }
            if (!lunch.getDishes().isEmpty()) {
                dayMenu.setLunch(lunch);
            }
            if (!dinner.getDishes().isEmpty()) {
                dayMenu.setDinner(dinner);
            }
            if (dayMenu.getBreakfast() != null || dayMenu.getLunch() != null || dayMenu.getDinner() != null) { // если хотя бы одно блюдо есть, добавляем этот день
                days.add(dayMenu);
            }
        }

        return new WeekPlanResponse(familyId, days);
    }

    private MealSlot findMealSlot(List<MealPlan> plans, int dayOfWeek, String mealType) {
        List<DishInfoResponse> dishes = plans.stream()
                .filter(p -> p.getDayOfWeek() == dayOfWeek && p.getMealType().equals(mealType))
                .map(plan -> plan.getDish() != null ? new DishInfoResponse(
                        plan.getDish().getId(),
                        plan.getDish().getName(),
                        plan.getDish().getDescription(),
                        plan.getDish().getSource()
                ) : null)
                .filter(Objects::nonNull)  // Убираем null на случай, если блюдо не загрузилось
                .toList();

        return new MealSlot(mealType, dishes);
    }
}
