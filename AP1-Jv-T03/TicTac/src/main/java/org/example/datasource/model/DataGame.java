package org.example.datasource.model;

import java.util.UUID;

public class DataGame {
    private final UUID uuid;
    private final DataGameField gameField;
    private final Integer computerMark;

    public DataGame(UUID uuid) {
        this.uuid = uuid;
        gameField = new DataGameField();
        computerMark = null;
    }

    public DataGame(UUID uuid, DataGameField gameField) {
        this.uuid = uuid;
        this.gameField = gameField;
        computerMark = null;
    }

    public DataGame(UUID uuid, DataGameField gameField, Integer computerMark) {
        this.uuid = uuid;
        this.gameField = gameField;
        this.computerMark = computerMark;
    }

    public UUID getUuid() {
        return uuid;
    }

    public DataGameField getGameField() {
        return gameField;
    }

    public Integer getComputerMark() {
        return computerMark;
    }
}
