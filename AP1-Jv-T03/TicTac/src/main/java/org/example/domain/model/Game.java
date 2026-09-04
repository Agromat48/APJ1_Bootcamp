package org.example.domain.model;

import java.util.UUID;

public class Game {
    private final UUID uuid;
    private final GameField gameField;
    private final Integer computerMark;

    public Game(UUID uuid) {
        this.uuid = uuid;
        gameField = new GameField();
        computerMark = null;
    }

    public Game(UUID uuid, GameField gameField) {
        this.uuid = uuid;
        this.gameField = gameField;
        computerMark = null;
    }

    public Game(UUID uuid, GameField gameField, Integer computerMark) {
        this.uuid = uuid;
        this.gameField = gameField;
        this.computerMark = computerMark;
    }

    public UUID getUuid() {
        return uuid;
    }

    public GameField getGameField() {
        return gameField;
    }

    public Integer getComputerMark() {
        return computerMark;
    }
}
