package org.example.di;

import org.example.datasource.mapper.DataGameMapper;
import org.example.web.mapper.WebGameMapper;
import org.example.datasource.repository.GameRepository;
import org.example.datasource.repository.GameStorage;
import org.example.datasource.service.GameServiceImpl;
import org.example.domain.service.GameService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TicTacConfiguration {
    @Bean
    public GameStorage gameStorage() {
        return new GameStorage();
    }

    @Bean
    public DataGameMapper dataGameMapper() {
        return new DataGameMapper();
    }

    @Bean
    public WebGameMapper webGameMapper() {
        return new WebGameMapper();
    }

    @Bean
    public GameRepository gameRepository(GameStorage gameStorage) {
        return new GameRepository(gameStorage);
    }

    @Bean
    public GameService gameService(GameRepository gameRepository, DataGameMapper dataGameMapper) {
        return new GameServiceImpl(gameRepository, dataGameMapper);
    }
}
