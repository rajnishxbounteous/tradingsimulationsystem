package com.example.tradingsimulationsystem.repository;

import com.example.tradingsimulationsystem.domain.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LedgerRepository extends JpaRepository<LedgerEntry, Long> {

    /**
     * Find all ledger entries for a given user.
     */
    List<LedgerEntry> findByUserId(Long userId);
}
