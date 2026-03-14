package com.example.tradingsimulationsystem.repository;

import com.example.tradingsimulationsystem.domain.TradeResult;
import com.example.tradingsimulationsystem.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeResultRepository extends JpaRepository<TradeResult, Long> {

    // Find all trades executed where the user was the buyer
    List<TradeResult> findByBuyer(User buyer);

    // Find all trades executed where the user was the seller
    List<TradeResult> findBySeller(User seller);
}
