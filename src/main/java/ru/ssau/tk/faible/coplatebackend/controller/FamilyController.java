package ru.ssau.tk.faible.coplatebackend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.faible.coplatebackend.dto.FamilyRequest;
import ru.ssau.tk.faible.coplatebackend.dto.FamilyResponse;
import ru.ssau.tk.faible.coplatebackend.service.FamilyService;

@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
@Slf4j
public class FamilyController {

    private final FamilyService familyService;

    @PostMapping()
    public ResponseEntity<FamilyResponse> createFamily(@RequestBody FamilyRequest request) {

        FamilyResponse familyResponse = familyService.createFamily(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(familyResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FamilyResponse> getFamilyById(@PathVariable Long id) {

        FamilyResponse familyResponse = familyService.getFamilyById(id);

        return ResponseEntity.status(HttpStatus.OK).body(familyResponse);
    }


}
