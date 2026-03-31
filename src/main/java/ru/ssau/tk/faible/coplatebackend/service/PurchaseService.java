package ru.ssau.tk.faible.coplatebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ssau.tk.faible.coplatebackend.dto.PurchasePutRequest;
import ru.ssau.tk.faible.coplatebackend.dto.PurchaseRequest;
import ru.ssau.tk.faible.coplatebackend.dto.PurchaseResponse;
import ru.ssau.tk.faible.coplatebackend.entity.*;
import ru.ssau.tk.faible.coplatebackend.exception.*;
import ru.ssau.tk.faible.coplatebackend.repository.DishRepository;
import ru.ssau.tk.faible.coplatebackend.repository.FamilyRepository;
import ru.ssau.tk.faible.coplatebackend.repository.PurchaseRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseService {

    private FamilyRepository familyRepository;
    private PurchaseRepository purchaseRepository;
    private DishRepository dishRepository;

    public PurchaseResponse addPurchase(Long familyId, PurchaseRequest request, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        // проверяем что пользователь - член семьи или админ
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException(familyId));

        List<Long> familyMembersIds = family.getUsers().stream().map(User::getId).toList();

        if (!familyMembersIds.contains(currentUser.getId()) && !Objects.equals(currentUser.getRole(), "admin")) {
            throw new ForbiddenException();
        }

        Dish dish = null;
        if (Objects.equals(request.getSource(), "dish")) {
            dish = dishRepository.findById(request.getDishId())
                    .orElseThrow(() -> new DishNotFoundException(request.getDishId()));
        }

        Purchase purchase = new Purchase(
                request.getName(),
                family,
                false,
                request.getQuantity(),
                request.getUnit(),
                request.getSource(),
                dish
        );

        Purchase savedPurchase = purchaseRepository.save(purchase);

        return new PurchaseResponse(savedPurchase);
    }

    public List<PurchaseResponse> getAllFamilyPurchases(Long familyId, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        // проверяем что пользователь - член семьи или админ
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException(familyId));

        List<Long> familyMembersIds = family.getUsers().stream().map(User::getId).toList();

        if (!familyMembersIds.contains(currentUser.getId()) && !Objects.equals(currentUser.getRole(), "admin")) {
            throw new ForbiddenException();
        }

        List<Purchase> purchases = purchaseRepository.getPurchasesByFamilyId(familyId);

        return Optional.ofNullable(purchases)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(purchase ->
                        new PurchaseResponse(
                                purchase.getId(),
                                purchase.getName(),
                                purchase.getQuantity(),
                                purchase.getUnit(),
                                purchase.getIsBought()
                        )
                ).toList();
    }

    public void clearPurchases(Long familyId, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        // проверяем что пользователь - член семьи или админ
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException(familyId));

        List<Long> familyMembersIds = family.getUsers().stream().map(User::getId).toList();

        if (!familyMembersIds.contains(currentUser.getId()) && !Objects.equals(currentUser.getRole(), "admin")) {
            throw new ForbiddenException();
        }

        List<Purchase> purchases = purchaseRepository.getPurchasesByFamilyId(familyId);

        purchaseRepository.deleteAll(purchases);
    }


    public PurchaseResponse changeBought(Long familyId, Long purchaseId, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        // проверяем что пользователь - член семьи или админ
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException(familyId));

        List<Long> familyMembersIds = family.getUsers().stream().map(User::getId).toList();

        if (!familyMembersIds.contains(currentUser.getId()) && !Objects.equals(currentUser.getRole(), "admin")) {
            throw new ForbiddenException();
        }

        Purchase purchase = purchaseRepository.getReferenceById(purchaseId);

        boolean isBought = purchase.getIsBought();
        purchase.setIsBought(!isBought);

        Purchase savedPurchase = purchaseRepository.save(purchase);

        return new PurchaseResponse(savedPurchase);
    }

    public void deletePurchase(Long familyId, Long purchaseId, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        // проверяем что пользователь - член семьи или админ
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException(familyId));

        List<Long> familyMembersIds = family.getUsers().stream().map(User::getId).toList();

        if (!familyMembersIds.contains(currentUser.getId()) && !Objects.equals(currentUser.getRole(), "admin")) {
            throw new ForbiddenException();
        }

        purchaseRepository.deleteById(purchaseId);
    }

    public PurchaseResponse changePurchase(Long familyId, Long purchaseId, PurchasePutRequest request, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        // проверяем что пользователь - член семьи или админ
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException(familyId));

        List<Long> familyMembersIds = family.getUsers().stream().map(User::getId).toList();

        if (!familyMembersIds.contains(currentUser.getId()) && !Objects.equals(currentUser.getRole(), "admin")) {
            throw new ForbiddenException();
        }

        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new PurchaseNotFoundException(purchaseId));

        if (request.getName() != null) purchase.setName(request.getName());
        if (request.getQuantity() != null) purchase.setQuantity(request.getQuantity());
        if (request.getUnit() != null) purchase.setUnit(request.getUnit());

        Purchase savedPurchase = purchaseRepository.save(purchase);

        return new PurchaseResponse(savedPurchase);
    }

    public List<PurchaseResponse> addPurchasesFromDish(Long familyId, Long dishId, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        // проверяем что пользователь - член семьи или админ
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException(familyId));

        List<Long> familyMembersIds = family.getUsers().stream().map(User::getId).toList();

        if (!familyMembersIds.contains(currentUser.getId()) && !Objects.equals(currentUser.getRole(), "admin")) {
            throw new ForbiddenException();
        }

        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(dishId));

        List<DishIngredient> ingredients = dish.getIngredients();

        List<Purchase> purchases = Optional.ofNullable(ingredients)
                .orElse(Collections.emptyList())
                .stream()
                .map(ingredient -> new Purchase(
                        ingredient.getName(),
                        family,
                        false,
                        ingredient.getQuantity(),
                        ingredient.getUnit(),
                        "dish",
                        dish)
                )
                .toList();

        List<Purchase> savedPurchases = purchaseRepository.saveAll(purchases);

        return savedPurchases.stream()
                .filter(Objects::nonNull)
                .map(purchase -> new PurchaseResponse(
                                purchase.getId(),
                                purchase.getName(),
                                purchase.getQuantity(),
                                purchase.getUnit(),
                                purchase.getIsBought())
                )
                .toList();
    }
}
