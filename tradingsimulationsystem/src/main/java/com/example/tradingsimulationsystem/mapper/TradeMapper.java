package com.example.tradingsimulationsystem.mapper;

import com.example.tradingsimulationsystem.domain.TradeRequest;
import com.example.tradingsimulationsystem.domain.TradeResult;
import com.example.tradingsimulationsystem.domain.User;
import com.example.tradingsimulationsystem.dto.TradeRequestDTO;
import com.example.tradingsimulationsystem.dto.TradeResultDTO;

import java.time.format.DateTimeFormatter;

public class TradeMapper {

    /**
     * Convert TradeRequestDTO into TradeRequest entity.
     * Requires User object fetched from repository.
     */
    public static TradeRequest toEntity(TradeRequestDTO dto, User user) {
        TradeRequest request = new TradeRequest();
        request.setUser(user);
        request.setSymbol(dto.getSymbol());
        request.setTradeType(dto.getTradeType());
        request.setQuantity(dto.getQuantity());
        return request;
    }

    /**
     * Convert TradeResult entity into TradeResultDTO for API response.
     */
    public static TradeResultDTO toDTO(TradeResult result) {
        return new TradeResultDTO(
                result.getStock().getSymbol(),
                result.getQuantity(),
                result.getExecutedPrice(),
                result.getStatus(), // SUCCESS or FAILED
                result.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }
}
