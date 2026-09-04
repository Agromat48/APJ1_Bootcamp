package org.example.web.controller;

import org.example.domain.model.Game;
import org.example.web.mapper.WebGameMapper;
import org.example.domain.service.GameService;
import org.example.web.model.WebGame;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class GameController {
    private final GameService gameService;
    private final WebGameMapper webGameMapper;

    public GameController(GameService gameService, WebGameMapper webGameMapper) {
        this.gameService = gameService;
        this.webGameMapper = webGameMapper;
    }

    @PostMapping("/game/{uuid}")
    public ResponseEntity<WebGame> makeMove(@PathVariable UUID uuid,
                                            @RequestBody WebGame webGame
    ) {
        Game mappedGame = webGameMapper.toDomain(webGame);
        Game game = new Game(uuid, mappedGame.getGameField());

        Game newGame = gameService.processMove(game);
        WebGame newWebGame = webGameMapper.toWeb(newGame);
        return ResponseEntity.ok(newWebGame);
    }
}
