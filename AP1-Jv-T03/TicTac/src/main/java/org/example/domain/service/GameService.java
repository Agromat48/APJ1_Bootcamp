package org.example.domain.service;

import org.example.domain.model.Game;
import org.example.domain.model.GameResult;

public interface GameService {
    Game makeNextMove(Game game);
    boolean validation(Game afterGame, Game beforeGame);
    boolean isOver(Game game);
    GameResult getWinner(Game game);
    Game processMove(Game incomingGame);
}
