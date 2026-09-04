package org.example.web.mapper;

import org.example.domain.model.Game;
import org.example.domain.model.GameField;
import org.example.web.model.WebGame;
import org.example.web.model.WebGameField;

public class WebGameMapper {
    public Game toDomain(WebGame webGame) {
        return new Game(webGame.getUuid(),
                new GameField(webGame.getGameField().getMatrix()));
    }

    public WebGame toWeb(Game game) {
        return new WebGame(game.getUuid(),
                new WebGameField(game.getGameField().getMatrix()));
    }
}
