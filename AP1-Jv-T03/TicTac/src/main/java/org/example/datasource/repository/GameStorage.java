package org.example.datasource.repository;

import org.example.datasource.model.DataGame;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameStorage {
    private final ConcurrentHashMap<UUID, DataGame> gamesMap;

    public GameStorage() {
         gamesMap = new ConcurrentHashMap<>();
    }

    public void save(DataGame game) {
        gamesMap.put(game.getUuid(), game);
    }

    public DataGame get(UUID uuid) {
        return gamesMap.get(uuid);
    }
}
