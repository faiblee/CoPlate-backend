package ru.ssau.tk.faible.coplatebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.faible.coplatebackend.dto.FamilyJoinRequest;
import ru.ssau.tk.faible.coplatebackend.dto.FamilyRequest;
import ru.ssau.tk.faible.coplatebackend.dto.FamilyResponse;
import ru.ssau.tk.faible.coplatebackend.entity.Family;
import ru.ssau.tk.faible.coplatebackend.entity.User;
import ru.ssau.tk.faible.coplatebackend.entity.UserDetailsImplementation;
import ru.ssau.tk.faible.coplatebackend.exception.*;
import ru.ssau.tk.faible.coplatebackend.repository.FamilyRepository;
import ru.ssau.tk.faible.coplatebackend.repository.UserRepository;
import ru.ssau.tk.faible.coplatebackend.util.InviteCodeGenerator;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;

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

        // ставим владельцу соответствующую семью
        owner.setFamily(savedFamily);
        userRepository.save(owner);

        return new FamilyResponse(savedFamily.getId(), savedFamily.getName(), savedFamily.getOwner().getId());
    }

    public FamilyResponse getFamilyById(Long id, UserDetailsImplementation currentUser) {

        // если пользователь не авторизован
        if (currentUser == null) {
            throw new UnauthorizedException();
        }

        Family family = familyRepository.findById(id).orElseThrow(() -> new FamilyNotFoundException(id));

        // если запрашивает не владелец семьи и не админ
        if ((!Objects.equals(currentUser.getId(), family.getOwner().getId()) && !currentUser.getRole().equals("ADMIN"))) {
            throw new ForbiddenException();
        }

        return new FamilyResponse(family.getId(), family.getName(), family.getOwner().getId());
    }

    public FamilyResponse joinFamily(FamilyJoinRequest request, UserDetailsImplementation currentUser) {
        // если пользователь не авторизован
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        // если запрашивается не текущий пользователь
        if ((!Objects.equals(currentUser.getId(), request.getUserId()) && !currentUser.getRole().equals("ADMIN"))) {
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

        return new FamilyResponse(family.getId(), family.getName(), family.getOwner().getId());
    }

    public List<User> getMembers(Long id, UserDetailsImplementation currentUser) {
        Family family = familyRepository.findById(id).orElseThrow(FamilyNotFoundException::new);


    }
}
