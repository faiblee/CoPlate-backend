package ru.ssau.tk.faible.coplatebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.faible.coplatebackend.entity.Purchase;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> getPurchasesByFamilyId(Long id);
}
