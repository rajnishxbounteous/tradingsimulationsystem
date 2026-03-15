//package com.example.tradingsimulationsystem.mapper;
//
//import com.example.tradingsimulationsystem.domain.TradeRequest;
//import com.example.tradingsimulationsystem.domain.TradeResult;
//import com.example.tradingsimulationsystem.dto.TradeRequestDTO;
//import com.example.tradingsimulationsystem.dto.TradeResultDTO;
//
//import java.time.format.DateTimeFormatter;
//
//public class TradeMapper {
//
//    public static TradeRequest toEntity(TradeRequestDTO dto) {
//        TradeRequest request = new TradeRequest();
//        request.setUserId(dto.getUserId());
//        request.setStockId(dto.getStockId());
//        request.setQuantity(dto.getQuantity());
//        request.setTradeType(dto.getTradeType());
//        return request;
//    }
//
//    public static TradeResultDTO toDTO(TradeResult result) {
//        return new TradeResultDTO(
//                result.getStock().getSymbol(),
//                result.getQuantity(),
//                result.getExecutedPrice(),
//                result.getTradeType().name(),
//                result.getExecutedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
//        );
//    }
//}
package com.example.tradingsimulationsystem.mapper;

import com.example.tradingsimulationsystem.domain.TradeRequest;
import com.example.tradingsimulationsystem.domain.TradeResult;
import com.example.tradingsimulationsystem.domain.User;
import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.dto.TradeRequestDTO;
import com.example.tradingsimulationsystem.dto.TradeResultDTO;

import java.time.format.DateTimeFormatter;

public class TradeMapper {

    /**
     * Convert TradeRequestDTO into TradeRequest entity.
     * Requires User and Stock objects fetched from repositories.
     */
    public static TradeRequest toEntity(TradeRequestDTO dto, User user, Stock stock) {
        TradeRequest request = new TradeRequest();
        request.setUser(user);
        request.setStock(stock);
        request.setTradeType(dto.getTradeType());
        request.setQuantity(dto.getQuantity());
        // Default order type and limit price can be set here if needed
        request.setOrderType(null); // or OrderType.MARKET if you want a default
        request.setLimitPrice(null); // extend DTO if you want limit orders
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
                "TRADE", // placeholder since TradeResult doesn’t have tradeType field
                result.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }
}
