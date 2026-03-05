package ru.ssau.tk.faible.coplatebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ssau.tk.faible.coplatebackend.dto.FamilyRequest;
import ru.ssau.tk.faible.coplatebackend.dto.FamilyResponse;
import ru.ssau.tk.faible.coplatebackend.entity.Family;
import ru.ssau.tk.faible.coplatebackend.entity.User;
import ru.ssau.tk.faible.coplatebackend.exception.FamilyNotFoundException;
import ru.ssau.tk.faible.coplatebackend.exception.UserNotFoundException;
import ru.ssau.tk.faible.coplatebackend.repository.FamilyRepository;
import ru.ssau.tk.faible.coplatebackend.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;

    public FamilyResponse createFamily(FamilyRequest familyRequest) {
        User owner = userRepository.findById(familyRequest.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException(familyRequest.getOwnerId()));

        Family family = new Family(familyRequest.getName(), owner);

        Family savedFamily = familyRepository.save(family);

        return new FamilyResponse(savedFamily.getId(), savedFamily.getName(), savedFamily.getOwner().getId());
    }

    public FamilyResponse getFamilyById(Long id) {

        Family family = familyRepository.findById(id).orElseThrow(() -> new FamilyNotFoundException(id));

        return new FamilyResponse(family.getId(), family.getName(), family.getOwner().getId());
    }
}
