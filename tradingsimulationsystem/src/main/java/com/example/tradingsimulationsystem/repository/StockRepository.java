package com.example.tradingsimulationsystem.repository;

import com.example.tradingsimulationsystem.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    /**
     * Find a stock by its symbol.
     */
    Optional<Stock> findBySymbol(String symbol);

    /**
     * Check if a stock exists by its symbol.
     */
    boolean existsBySymbol(String symbol);
}
