package ru.ssau.tk.faible.coplatebackend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.faible.coplatebackend.dto.DishCreateCustomRequest;
import ru.ssau.tk.faible.coplatebackend.dto.DishPutRequest;
import ru.ssau.tk.faible.coplatebackend.dto.DishResponse;
import ru.ssau.tk.faible.coplatebackend.entity.UserDetailsImplementation;
import ru.ssau.tk.faible.coplatebackend.service.DishService;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
@Slf4j
public class DishController {

    DishService dishService;

    @GetMapping("/{id}")
    public ResponseEntity<DishResponse> getDishById(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        DishResponse dishResponse = dishService.getDishById(id, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(dishResponse);
    }

    @PostMapping("/add_custom")
    public ResponseEntity<DishResponse> createCustomDish(@RequestBody DishCreateCustomRequest request, @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        DishResponse dishResponse = dishService.createCustomDish(request, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(dishResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DishResponse> updateDish(@RequestBody DishPutRequest request, @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        DishResponse dishResponse = dishService.updateDish(request, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(dishResponse);
    }

    @GetMapping("/library")
    public ResponseEntity<List<DishResponse>> getLibraryDishes(@AuthenticationPrincipal UserDetailsImplementation currentUser) {

        List<DishResponse> dishes = dishService.getLibraryDishes(currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(dishes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDishById(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        dishService.deleteById(id, currentUser);

        return ResponseEntity.noContent().build();
    }
}
