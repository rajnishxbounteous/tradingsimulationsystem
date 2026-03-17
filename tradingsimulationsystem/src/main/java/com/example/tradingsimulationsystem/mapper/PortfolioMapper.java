package com.example.tradingsimulationsystem.mapper;

import com.example.tradingsimulationsystem.domain.UserPortfolio;
import com.example.tradingsimulationsystem.dto.UserPortfolioDTO;

public class PortfolioMapper {

    public static UserPortfolioDTO toDTO(UserPortfolio portfolio) {
        return new UserPortfolioDTO(
                portfolio.getStock().getSymbol(),
                portfolio.getQuantity(),
                portfolio.getStock().getCurrentPrice()
        );
    }
}
