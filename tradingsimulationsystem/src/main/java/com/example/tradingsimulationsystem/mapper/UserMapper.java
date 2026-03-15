package com.example.tradingsimulationsystem.mapper;

import com.example.tradingsimulationsystem.domain.User;
import com.example.tradingsimulationsystem.dto.UserDTO;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getBalance(),
                user.getMarginAllowed(),
                user.getMarginUsed()
        );
    }
}
