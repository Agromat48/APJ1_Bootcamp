package org.example.datasource.repository;

import org.example.datasource.model.DataGame;

import java.util.UUID;

public class GameRepository {
    private final GameStorage gameStorage;

    public GameRepository(GameStorage gameStorage) {
        this.gameStorage = gameStorage;
    }

    public void save(DataGame game) {
        gameStorage.save(game);
    }

    public DataGame get(UUID uuid) {
        return gameStorage.get(uuid);
    }
}
