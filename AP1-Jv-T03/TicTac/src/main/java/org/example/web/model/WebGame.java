package org.example.web.model;

import java.util.UUID;

public class WebGame {
    private UUID uuid;
    private WebGameField gameField;

    public WebGame() {
    }

    public WebGame(UUID uuid) {
        this.uuid = uuid;
        this.gameField = new WebGameField();
    }

    public WebGame(UUID uuid, WebGameField gameField) {
        this.uuid = uuid;
        this.gameField = gameField;
    }

    public UUID getUuid() {
        return uuid;
    }

    public WebGameField getGameField() {
        return gameField;
    }
}
