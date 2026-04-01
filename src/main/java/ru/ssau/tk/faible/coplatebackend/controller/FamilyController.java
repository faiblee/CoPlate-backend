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
import ru.ssau.tk.faible.coplatebackend.service.PurchaseService;

import java.util.List;

@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
@Slf4j
public class FamilyController {

    private final FamilyService familyService;
    private final MealPlanService mealPlanService;
    private final PurchaseService purchaseService;

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
    public ResponseEntity<List<UserResponse>> listMembers(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        List<UserResponse> users = familyService.getMembers(id, currentUser);

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
            @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        FamilyResponse familyResponse = familyService.updateFamily(request, id, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(familyResponse);
    }

    @PutMapping("/{id}/kick")
    public ResponseEntity<List<UserResponse>> leaveFromFamily(
            @PathVariable Long id,
            @RequestBody Long userId,
            @AuthenticationPrincipal UserDetailsImplementation currentUser) {
        List<UserResponse> users = familyService.kickFromFamily(id, userId, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFamily(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        familyService.deleteFamily(id, currentUser);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/meal-plans")
    public ResponseEntity<MealPlanResponse> addDishToMealPlan(@PathVariable Long id,
                                                              @RequestBody MealPlanCreateRequest request,
                                                              @AuthenticationPrincipal UserDetailsImplementation currentUser) {
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

    @PostMapping("/{id}/purchases")
    public ResponseEntity<PurchaseResponse> addPurchase(@PathVariable Long id,
                                                        @RequestBody PurchaseRequest request,
                                                        @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        PurchaseResponse response = purchaseService.addPurchase(id, request, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}/purchases")
    public ResponseEntity<List<PurchaseResponse>> addPurchase(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        List<PurchaseResponse> responses = purchaseService.getAllFamilyPurchases(id, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @DeleteMapping("/{id}/purchases")
    public ResponseEntity<Void> clearPurchases(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        purchaseService.clearPurchases(id, currentUser);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/purchases/{purchaseId}/bought")
    public ResponseEntity<PurchaseResponse> changeBought(@PathVariable Long id,
                                                         @PathVariable Long purchaseId,
                                                         @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        PurchaseResponse response = purchaseService.changeBought(id, purchaseId, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}/purchases/{purchaseId}")
    public ResponseEntity<PurchaseResponse> changePurchase(@PathVariable Long id,
                                                           @PathVariable Long purchaseId,
                                                           @RequestBody PurchasePutRequest request,
                                                           @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        PurchaseResponse response = purchaseService.changePurchase(id, purchaseId, request, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}/purchases/{purchaseId}")
    public ResponseEntity<Void> deletePurchase(@PathVariable Long id,
                                               @PathVariable Long purchaseId,
                                               @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        purchaseService.deletePurchase(id, purchaseId, currentUser);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/purchases/add-purchases-from-dish/{dishId}")
    public ResponseEntity<List<PurchaseResponse>> addPurchasesFromDishToPurchases(@PathVariable Long id,
                                                                                  @PathVariable Long dishId,
                                                                                  @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        List<PurchaseResponse> purchaseResponses = purchaseService.addPurchasesFromDish(id, dishId, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(purchaseResponses);

    }
}
