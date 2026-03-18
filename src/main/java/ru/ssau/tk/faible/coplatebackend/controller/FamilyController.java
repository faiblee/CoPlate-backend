package ru.ssau.tk.faible.coplatebackend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.faible.coplatebackend.dto.FamilyJoinRequest;
import ru.ssau.tk.faible.coplatebackend.dto.FamilyPutRequest;
import ru.ssau.tk.faible.coplatebackend.dto.FamilyRequest;
import ru.ssau.tk.faible.coplatebackend.dto.FamilyResponse;
import ru.ssau.tk.faible.coplatebackend.entity.User;
import ru.ssau.tk.faible.coplatebackend.entity.UserDetailsImplementation;
import ru.ssau.tk.faible.coplatebackend.service.FamilyService;

import java.util.List;

@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
@Slf4j
public class FamilyController {

    private final FamilyService familyService;

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
    public ResponseEntity<List<User>> leaveFromFamily(
            @PathVariable Long id,
            @RequestBody Long userId,
            @AuthenticationPrincipal UserDetailsImplementation currentUser
    ) {
        List<User> members = familyService.kickFromFamily(id, userId, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(members);
    }

    @DeleteMapping("/{id}")
    public void deleteFamily(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImplementation currentUser) {
        familyService.deleteFamily(id, currentUser);
    }
}
