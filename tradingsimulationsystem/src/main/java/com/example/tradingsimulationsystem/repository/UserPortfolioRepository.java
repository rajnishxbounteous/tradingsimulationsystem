package com.example.tradingsimulationsystem.repository;

import com.example.tradingsimulationsystem.domain.UserPortfolio;
import com.example.tradingsimulationsystem.domain.User;
import com.example.tradingsimulationsystem.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPortfolioRepository extends JpaRepository<UserPortfolio, Long> {

    // Find all portfolio entries for a given user
    List<UserPortfolio> findByUser(User user);

    // Find portfolio entry for a specific user and stock
    Optional<UserPortfolio> findByUserAndStock(User user, Stock stock);
}
