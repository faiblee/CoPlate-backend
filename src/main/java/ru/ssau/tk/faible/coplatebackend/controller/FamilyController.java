package ru.ssau.tk.faible.coplatebackend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.faible.coplatebackend.dto.*;
import ru.ssau.tk.faible.coplatebackend.entity.User;
import ru.ssau.tk.faible.coplatebackend.entity.UserDetailsImplementation;
import ru.ssau.tk.faible.coplatebackend.service.FamilyService;
import ru.ssau.tk.faible.coplatebackend.service.MealPlanService;

import java.util.List;

@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
@Slf4j
public class FamilyController {

    private final FamilyService familyService;
    private final MealPlanService mealPlanService;

    @PostMapping()
    public ResponseEntity<FamilyResponse> createFamily(@RequestBody FamilyRequest request, @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        FamilyResponse familyResponse = familyService.createFamily(request, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(familyResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FamilyResponse> getFamilyById(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        FamilyResponse familyResponse = familyService.getFamilyById(id, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(familyResponse);
    }

    @PostMapping("/join")
    public ResponseEntity<FamilyResponse> joinFamily(@RequestBody FamilyJoinRequest request, @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        FamilyResponse familyResponse = familyService.joinFamily(request, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(familyResponse);
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<User>> listMembers(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        List<User> users = familyService.getMembers(id, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/{id}/invite_code")
    public ResponseEntity<String> getInviteCode(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        String invite_code = familyService.getInviteCode(id, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(invite_code);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FamilyResponse> updateFamily(
            @PathVariable Long id,
            @RequestBody FamilyPutRequest request,
            @AuthenticationPrincipal UserDetailsImplementation currentUser
    ) {

        FamilyResponse familyResponse = familyService.updateFamily(request, id, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(familyResponse);
    }

    @PutMapping("/{id}/kick")
    public ResponseEntity<List<Long>> leaveFromFamily(
            @PathVariable Long id,
            @RequestBody Long userId,
            @AuthenticationPrincipal UserDetailsImplementation currentUser
    ) {
        List<Long> ids = familyService.kickFromFamily(id, userId, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(ids);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFamily(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        familyService.deleteFamily(id, currentUser);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/meal-plans")
    public ResponseEntity<MealPlanResponse> addDishToMealPlan(@PathVariable Long id,
                                                              @RequestBody MealPlanCreateRequest request,
                                                              @AuthenticationPrincipal UserDetailsImplementation currentUser)
    {
        MealPlanAddRequest addRequest = new MealPlanAddRequest(id, request.getDishId(), request.getDayOfWeek(), request.getMealType());

        MealPlanResponse mealPlanResponse = mealPlanService.addDishToMealPlan(addRequest, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(mealPlanResponse);
    }

    @DeleteMapping("/{id}/meal-plans/week")
    public ResponseEntity<Void> clearWeekMealPlan(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        mealPlanService.clearWeekPlan(id, currentUser);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/meal-plans/week")
    public ResponseEntity<WeekPlanResponse> getWeekFamilyMealPlan(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        WeekPlanResponse response = mealPlanService.getWeekFamilyMealPlan(id, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}/dishes")
    public ResponseEntity<List<DishInfoResponse>> getAllFamilyDishes(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        List<DishInfoResponse> dishes = familyService.getAllFamilyDishes(id, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(dishes);
    }
}
