package com.example.tradingsimulationsystem.repository;

import com.example.tradingsimulationsystem.domain.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LedgerRepository extends JpaRepository<LedgerEntry, Long> {

    // Find all ledger entries for a specific user
    List<LedgerEntry> findByUserId(Long userId);

    // Find all ledger entries for a specific stock symbol
    List<LedgerEntry> findByStockSymbol(String stockSymbol);

    // Optional: find all entries for a user and stock together
    List<LedgerEntry> findByUserIdAndStockSymbol(Long userId, String stockSymbol);
}
