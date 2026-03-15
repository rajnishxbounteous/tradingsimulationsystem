package com.example.tradingsimulationsystem.mapper;

import com.example.tradingsimulationsystem.dto.MarketStatusDTO;

public class MarketMapper {

    public static MarketStatusDTO toDTO(boolean open) {
        String message = open ? "Market is OPEN" : "Market is CLOSED";
        return new MarketStatusDTO(open, message);
    }
}
