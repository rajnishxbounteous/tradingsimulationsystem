//package com.example.tradingsimulationsystem.service;
//
//import com.example.tradingsimulationsystem.domain.User;
//import com.example.tradingsimulationsystem.domain.UserPortfolio;
//import com.example.tradingsimulationsystem.repository.UserPortfolioRepository;
//import com.example.tradingsimulationsystem.repository.UserRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class PortfolioService {
//
//    private final UserRepository userRepository;
//    private final UserPortfolioRepository portfolioRepository;
//
//    public PortfolioService(UserRepository userRepository,
//                            UserPortfolioRepository portfolioRepository) {
//        this.userRepository = userRepository;
//        this.portfolioRepository = portfolioRepository;
//    }
//
//    /**
//     * Get all portfolio holdings for a given user.
//     */
//    public List<UserPortfolio> getUserPortfolio(User user) {
//        return portfolioRepository.findByUser(user);
//    }
//
//    /**
//     * Get the current cash balance of a user.
//     */
//    public double getUserBalance(User user) {
//        return user.getBalance();
//    }
//
//    /**
//     * Get margin usage details for a user.
//     */
//    public String getMarginStatus(User user) {
//        return "Margin Allowed: " + user.getMarginAllowed() +
//                ", Margin Used: " + user.getMarginUsed();
//    }
//
//    /**
//     * Refresh user from DB (ensures latest state).
//     */
//    public User refreshUser(Long userId) {
//        return userRepository.findById(userId).orElseThrow(
//                () -> new RuntimeException("User not found")
//        );
//    }
//}



package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.User;
import com.example.tradingsimulationsystem.domain.UserPortfolio;
import com.example.tradingsimulationsystem.repository.UserPortfolioRepository;
import com.example.tradingsimulationsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {

    private final UserRepository userRepository;
    private final UserPortfolioRepository portfolioRepository;

    public PortfolioService(UserRepository userRepository, UserPortfolioRepository portfolioRepository) {
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
    }

    public User refreshUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<UserPortfolio> getUserPortfolio(User user) {
        return portfolioRepository.findByUser(user);
    }

    public double getUserBalance(User user) {
        return user.getBalance();
    }

    public String getMarginStatus(User user) {
        return "Allowed: " + user.getMarginAllowed() + ", Used: " + user.getMarginUsed();
    }
}
