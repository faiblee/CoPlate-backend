package ru.ssau.tk.faible.coplatebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.ssau.tk.faible.coplatebackend.dto.*;
import ru.ssau.tk.faible.coplatebackend.entity.*;
import ru.ssau.tk.faible.coplatebackend.exception.*;
import ru.ssau.tk.faible.coplatebackend.repository.FamilyRepository;
import ru.ssau.tk.faible.coplatebackend.repository.MealPlanRepository;
import ru.ssau.tk.faible.coplatebackend.repository.UserRepository;
import ru.ssau.tk.faible.coplatebackend.util.InviteCodeGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final MealPlanRepository mealPlanRepository;

    @Transactional
    public FamilyResponse createFamily(FamilyRequest familyRequest, UserDetailsImplementation currentUser) {

        // если пользователь не авторизован
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        // если запрашивается не текущий пользователь
        if ((!Objects.equals(currentUser.getId(), familyRequest.getOwnerId()) && !currentUser.getRole().equals("ADMIN"))) {
            throw new ForbiddenException();
        }

        User owner = userRepository.findById(familyRequest.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException(familyRequest.getOwnerId()));

        if (owner.getFamily() != null) {
            throw new AlreadyFamilyException(owner.getId());
        }

        String inviteCode;
        do {
            inviteCode = InviteCodeGenerator.generate(); // TODO: странно, мб можно сделать лучше
        } while (familyRepository.existsByInviteCode(inviteCode)); // пока код не станет уникальным, генерируем новый



        Family family = new Family(familyRequest.getName(), owner, inviteCode);

        Family savedFamily = familyRepository.save(family);

        // Заполняем MealPlan 21 значениями на всю неделю на 3 приема пищи
        for (int i = 1; i <= 7; i++) {
            MealPlan mealPlanBreakfast = new MealPlan(savedFamily, null, i, "breakfast");
            MealPlan mealPlanLunch = new MealPlan(savedFamily, null, i, "lunch");
            MealPlan mealPlanBDinner = new MealPlan(savedFamily, null, i, "dinner");

            mealPlanRepository.save(mealPlanBreakfast);
            mealPlanRepository.save(mealPlanLunch);
            mealPlanRepository.save(mealPlanBDinner);
        }

        // ставим владельцу соответствующую семью
        owner.setFamily(savedFamily);
        userRepository.save(owner);

        return new FamilyResponse(savedFamily);
    }

    public FamilyResponse getFamilyById(Long id, UserDetailsImplementation currentUser) {

        // если пользователь не авторизован
        if (currentUser == null) {
            throw new UnauthorizedException();
        }

        Family family = familyRepository.findById(id).orElseThrow(() -> new FamilyNotFoundException(id));

        // если запрашивает не владелец семьи и не админ
        if (!Objects.equals(currentUser.getId(), family.getOwner().getId())
                && !Objects.equals(currentUser.getFamily().getId(), family.getId())
                && !currentUser.getRole().equals("admin")) {
            throw new ForbiddenException();
        }

        return new FamilyResponse(family);
    }

    public String getInviteCode(Long id, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }

        Family family = familyRepository.findById(id).orElseThrow(() -> new FamilyNotFoundException(id));

        if (!Objects.equals(currentUser.getFamily().getId(), family.getId()) && !currentUser.getRole().equals("admin")) {
            throw new ForbiddenException();
        }

        return family.getInviteCode();
    }

    public FamilyResponse joinFamily(FamilyJoinRequest request, UserDetailsImplementation currentUser) {
        // если пользователь не авторизован
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        // если запрашивается не текущий пользователь
        if ((!Objects.equals(currentUser.getId(), request.getUserId()) && !currentUser.getRole().equals("admin"))) {
            throw new ForbiddenException();
        }

        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserNotFoundException(request.getUserId()));

        if (user.getFamily() != null) {
            throw new AlreadyFamilyException(request.getUserId());
        }

        Family family = familyRepository.findFamilyByInviteCode(request.getInviteCode())
                .orElseThrow(FamilyNotFoundException::new);

        user.setFamily(family);
        userRepository.save(user);

        return new FamilyResponse(family);
    }

    public List<User> getMembers(Long id, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }

        if (!Objects.equals(currentUser.getFamily().getId(), id) && !Objects.equals(currentUser.getRole(), "admin")) {
            throw new ForbiddenException();
        }

        Family family = familyRepository.findById(id).orElseThrow(FamilyNotFoundException::new);

        return family.getUsers();
    }

    public FamilyResponse updateFamily(FamilyPutRequest request, Long id, UserDetailsImplementation currentUser) {

        // если пользователь не авторизован
        if (currentUser == null) {
            throw new UnauthorizedException();
        }

        Family family = familyRepository.findById(id).orElseThrow(FamilyNotFoundException::new);

        if (!Objects.equals(family.getOwner().getId(), currentUser.getId()) && currentUser.getRole().equals("admin")) {
            throw new ForbiddenException();
        }

        if (request.getName() != null) { // если передан новый name
            family.setName(request.getName());
        }
        if (request.getOwnerId() != null) { // если передан новый владелец
            User new_owner = userRepository.findById(request.getOwnerId()).orElseThrow(
                    () -> new UserNotFoundException(request.getOwnerId()));
            family.setOwner(new_owner);
        }

        Family saved_fam = familyRepository.save(family);

        return new FamilyResponse(saved_fam);
    }

    public void deleteFamily(Long id, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }

        Family family = familyRepository.findById(id).orElseThrow(FamilyNotFoundException::new);

        if (!Objects.equals(family.getOwner().getId(), currentUser.getId()) && !Objects.equals(currentUser.getRole(), "admin")) {
            throw new ForbiddenException();
        }

        familyRepository.deleteById(family.getId());
    }

    public List<Long> kickFromFamily(Long id, Long userId, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }

        Family family = familyRepository.findById(id).orElseThrow(FamilyNotFoundException::new);

        User user_to_kick = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        // Если запрашивает удаление не админ не владелец семьи и не сам пользователь
        if (!Objects.equals(family.getOwner().getId(), currentUser.getId())
                && currentUser.getRole().equals("admin")
                && !Objects.equals(currentUser.getId(), userId)
        ) {
            throw new ForbiddenException();
        }

        // Если хотим выгнать владельца семьи
        if (Objects.equals(family.getOwner().getId(), userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя выгнать владельца семьи");
        }

        user_to_kick.setFamily(null); // удаляем семью у пользователя

        userRepository.save(user_to_kick);

        return family.getUsers().stream().map(User::getId).toList();
    }

    public List<DishInfoResponse> getAllFamilyDishes(Long familyId, UserDetailsImplementation currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException();
        }

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException(familyId));

        List<Long> familyMembersIds = family.getUsers().stream().map(User::getId).toList();

        if (!familyMembersIds.contains(currentUser.getId()) && !Objects.equals(currentUser.getRole(), "admin")) {
            throw new ForbiddenException();
        }

        List<Dish> dishes = family.getDishes();

        return Optional.ofNullable(dishes)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull) // пропускаем null-объекты
                .map(dish -> new DishInfoResponse(
                        dish.getId(),
                        dish.getName(),
                        dish.getDescription(),
                        dish.getSource()
                ))
                .toList();
    }
}
