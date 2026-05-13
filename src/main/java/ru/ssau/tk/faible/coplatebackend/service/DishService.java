package ru.ssau.tk.faible.coplatebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ssau.tk.faible.coplatebackend.dto.*;
import ru.ssau.tk.faible.coplatebackend.entity.*;
import ru.ssau.tk.faible.coplatebackend.exception.*;
import ru.ssau.tk.faible.coplatebackend.repository.DishIngredientRepository;
import ru.ssau.tk.faible.coplatebackend.repository.DishRepository;
import ru.ssau.tk.faible.coplatebackend.repository.FamilyRepository;
import ru.ssau.tk.faible.coplatebackend.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DishService {

    private final DishRepository dishRepository;
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final DishIngredientRepository dishIngredientRepository;

    public DishResponse createCustomDish(DishCreateCustomRequest request, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        if (!Objects.equals(request.getSource(), "custom") || request.getOwnerId() == null || request.getFamilyId() == null) {
            throw new BadCreateDishRequest();
        }
        if (!request.getOwnerId().equals(currentUser.getId()) && !Objects.equals(currentUser.getRole(), "admin")) {
            throw new ForbiddenException();
        }

        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException(request.getOwnerId()));

        Family family = familyRepository.findById(request.getFamilyId())
                .orElseThrow(() -> new FamilyNotFoundException(request.getFamilyId()));

        Dish dish = new Dish(request.getName(), request.getDescription(), request.getSource(), family, owner);

        // добавляем все ингредиенты
        Dish savedDish = dishRepository.save(dish);

        List<DishIngredientRequest> ingredientRequests = request.getIngredients();

        List<DishIngredient> ingredients = Optional.ofNullable(ingredientRequests)
                .orElse(Collections.emptyList())
                .stream()
                .map(ingredientRequest -> new DishIngredient(
                        ingredientRequest.getName(),
                        savedDish,
                        ingredientRequest.getQuantity(),
                        ingredientRequest.getUnit()
                        )
                ).toList();

        dishIngredientRepository.saveAll(ingredients);

        savedDish.getIngredients().addAll(ingredients);

        return new DishResponse(savedDish);
    }

    public DishResponse updateDish(DishPutRequest request, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        Dish dish = dishRepository.findById(request.getDishId())
                .orElseThrow(() -> new DishNotFoundException(request.getDishId()));
        if (!dish.getFamily().getId().equals(currentUser.getFamily().getId()) && !Objects.equals(currentUser.getRole(), "admin")) {
            throw new ForbiddenException();
        }
        if (request.getName() != null) dish.setName(request.getName());
        if (request.getDescription() != null) dish.setDescription(request.getDescription());
        dishRepository.save(dish);

        return new DishResponse(dish);
    }

//    public DishResponse getDishById(Long id, UserDetailsImplementation currentUser) {
//        if (currentUser == null) {
//            throw new UnauthorizedException();
//        }
//        Dish dish = dishRepository.findById(id)
//                .orElseThrow(() -> new DishNotFoundException(id));
//        if (!dish.getFamily().getId().equals(currentUser.getFamily().getId())
//                && !Objects.equals(currentUser.getRole(), "admin")
//                && !Objects.equals(dish.getSource(), "library"))
//        {
//            throw new ForbiddenException();
//        }
//        if (dish.getSource().equals("library")) {
//            List<DishIngredientResponse> ingredients = Optional.ofNullable(dish.getIngredients())
//                    .orElse(Collections.emptyList())
//                    .stream()
//                    .map(ingredient -> new DishIngredientResponse(
//                            ingredient.getId(),
//                            ingredient.getName(),
//                            ingredient.getQuantity(),
//                            ingredient.getUnit()
//                    )).toList();
//            return new DishResponse(dish.getId(), dish.getName(), dish.getDescription(), ingredients);
//        } else {
//            return new DishResponse(dish);
//        }
//    }

    public DishResponse getDishById(Long id, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new DishNotFoundException(id));
        final boolean isLibrary = Objects.equals(dish.getSource(), "library");
        if (!isLibrary) {
            if (!Objects.equals(currentUser.getRole(), "admin")) {
                if (dish.getFamily() == null || currentUser.getFamily() == null) {
                    throw new ForbiddenException();
                }
                if (!Objects.equals(dish.getFamily().getId(), currentUser.getFamily().getId())) {
                    throw new ForbiddenException();
                }
            }
        }
        if (isLibrary) {
            List<DishIngredientResponse> ingredients = Optional.ofNullable(dish.getIngredients())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(ingredient -> new DishIngredientResponse(
                            ingredient.getId(),
                            ingredient.getName(),
                            ingredient.getQuantity(),
                            ingredient.getUnit()
                    ))
                    .toList();
            return new DishResponse(dish.getId(), dish.getName(), dish.getDescription(), ingredients);
        }
        return new DishResponse(dish);
    }

    public List<DishResponse> getLibraryDishes(UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }

        List<Dish> dishes = dishRepository.getAllBySourceEquals("library");

        return dishes.stream().map(DishResponse::new).toList();
    }

    public void deleteById(Long id, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new DishNotFoundException(id));
        if (!dish.getFamily().getId().equals(currentUser.getFamily().getId()) && !Objects.equals(currentUser.getRole(), "admin")) {
            throw new ForbiddenException();
        }

        dishRepository.deleteById(id);
    }

    public List<DishIngredientResponse> getDishIngredients(Long id, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new DishNotFoundException(id));
        if (!dish.getFamily().getId().equals(currentUser.getFamily().getId()) && !Objects.equals(currentUser.getRole(), "admin")) {
            throw new ForbiddenException();
        }

        List<DishIngredient> ingredients = dish.getIngredients();

        return Optional.ofNullable(ingredients)
                .orElse(Collections.emptyList())
                .stream()
                .map(ingredient -> new DishIngredientResponse(
                        ingredient.getId(),
                        ingredient.getName(),
                        ingredient.getQuantity(),
                        ingredient.getUnit()
                )).toList();
    }
}
