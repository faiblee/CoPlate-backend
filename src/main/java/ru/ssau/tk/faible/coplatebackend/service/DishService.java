package ru.ssau.tk.faible.coplatebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ssau.tk.faible.coplatebackend.dto.DishCreateCustomRequest;
import ru.ssau.tk.faible.coplatebackend.dto.DishResponse;
import ru.ssau.tk.faible.coplatebackend.dto.DishPutRequest;
import ru.ssau.tk.faible.coplatebackend.entity.Dish;
import ru.ssau.tk.faible.coplatebackend.entity.Family;
import ru.ssau.tk.faible.coplatebackend.entity.User;
import ru.ssau.tk.faible.coplatebackend.entity.UserDetailsImplementation;
import ru.ssau.tk.faible.coplatebackend.exception.*;
import ru.ssau.tk.faible.coplatebackend.repository.DishRepository;
import ru.ssau.tk.faible.coplatebackend.repository.FamilyRepository;
import ru.ssau.tk.faible.coplatebackend.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DishService {

    DishRepository dishRepository;
    UserRepository userRepository;
    FamilyRepository familyRepository;

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

        dishRepository.save(dish);

        return new DishResponse(dish);
    }

    public DishResponse updateDish(DishPutRequest request, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        Dish dish = dishRepository.findById(request.getDishId())
                .orElseThrow(() -> new DishNotFoundException(request.getDishId()));
        if (!dish.getCreatedBy().getId().equals(currentUser.getId()) && !Objects.equals(currentUser.getRole(), "admin")) {
            throw new ForbiddenException();
        }
        if (request.getName() != null) dish.setName(request.getName());
        if (request.getDescription() != null) dish.setDescription(request.getDescription());
        dishRepository.save(dish);

        return new DishResponse(dish);
    }

    public DishResponse getDishById(Long id, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new DishNotFoundException(id));
        if (!dish.getCreatedBy().getId().equals(currentUser.getId())
                && !Objects.equals(currentUser.getRole(), "admin")
                && !Objects.equals(dish.getSource(), "library"))
        {
            throw new ForbiddenException();
        }
        if (dish.getSource().equals("library")) {
            return new DishResponse(dish.getId(), dish.getName(), dish.getDescription());
        } else {
            return new DishResponse(dish);
        }
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
        Dish dish = dishRepository.findById(id).orElseThrow(() -> new DishNotFoundException(id));
        if (!Objects.equals(dish.getCreatedBy().getId(), currentUser.getId()) && !Objects.equals(currentUser.getRole(), "admin")) {
            throw new ForbiddenException();
        }

        dishRepository.deleteById(id);
    }
}
