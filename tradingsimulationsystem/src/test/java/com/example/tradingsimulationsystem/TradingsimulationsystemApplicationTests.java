package com.example.tradingsimulationsystem;

import com.example.tradingsimulationsystem.service.FinnhubService;
import com.example.tradingsimulationsystem.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TradingsimulationsystemApplicationTests {

    @MockBean
    private FinnhubService finnhubService;

    @MockBean
    private StockRepository stockRepository;

    @Test
    void contextLoads() {
        // If this runs, the context started successfully
    }
}

