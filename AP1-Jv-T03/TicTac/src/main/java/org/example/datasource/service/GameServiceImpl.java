package org.example.datasource.service;

import org.example.datasource.mapper.DataGameMapper;
import org.example.datasource.model.DataGame;
import org.example.datasource.repository.GameRepository;
import org.example.domain.exception.InvalidMoveException;
import org.example.domain.model.Game;
import org.example.domain.model.GameField;
import org.example.domain.model.GameResult;
import org.example.domain.service.GameService;

public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final DataGameMapper dataGameMapper;

    public GameServiceImpl(GameRepository gameRepository, DataGameMapper gameMapper) {
        this.gameRepository = gameRepository;
        this.dataGameMapper = gameMapper;
    }

    @Override
    public Game makeNextMove(Game game) {
        return makeNextMove(game, -1);
    }

    private Game makeNextMove(Game game, int computerMark) {
        int[][] matrix = copyMatrix(game.getGameField().getMatrix());

        int bestScore = Integer.MIN_VALUE;
        int bestI = -1;
        int bestJ = -1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][j] = computerMark;
                    int score = minimax(matrix, false, computerMark);
                    matrix[i][j] = 0;

                    if (score > bestScore) {
                        bestScore = score;
                        bestI = i;
                        bestJ = j;
                    }
                }
            }
        }

        if (bestI == -1) {
            return game;
        }

        int[][] newMatrix = copyMatrix(game.getGameField().getMatrix());
        newMatrix[bestI][bestJ] = computerMark;

        return new Game(game.getUuid(), new GameField(newMatrix));
    }

    private int minimax(int[][] matrix, boolean isMaximizing, int computerMark) {
        GameResult result = getWinner(matrix);

        if (result != GameResult.NOT_FINISHED) {
            if (result == GameResult.DRAW) {
                return 0;
            }
            boolean computerWon = (result == GameResult.X_WINS && computerMark == 1)
                    || (result == GameResult.O_WINS && computerMark == -1);
            return computerWon ? 1 : -1;
        }

        int mark = isMaximizing ? computerMark : -computerMark;
        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][j] = mark;
                    int score = minimax(matrix, !isMaximizing, computerMark);
                    matrix[i][j] = 0;

                    bestScore = isMaximizing
                            ? Math.max(bestScore, score)
                            : Math.min(bestScore, score);
                }
            }
        }

        return bestScore;
    }

    @Override
    public boolean validation(Game afterGame, Game beforeGame) {
        int[][] after = afterGame.getGameField().getMatrix();
        int[][] before = beforeGame.getGameField().getMatrix();

        int count = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (before[i][j] != 0 && before[i][j] != after[i][j]) {
                    return false;
                }
                if (before[i][j] == 0 && after[i][j] != 0) {
                    count++;
                }
            }
        }

        return count == 1;
    }

    @Override
    public boolean isOver(Game game) {
        return getWinner(game) != GameResult.NOT_FINISHED;
    }

    @Override
    public GameResult   getWinner(Game game) {
        return getWinner(game.getGameField().getMatrix());
    }

    @Override
    public Game processMove(Game incomingGame) {
        DataGame storedGame = gameRepository.get(incomingGame.getUuid());

        Game oldGame;
        if (storedGame == null) {
            oldGame = new Game(incomingGame.getUuid());
        } else {
            oldGame = dataGameMapper.toDomain(storedGame);
        }

        if (isOver(oldGame)) {
            throw new InvalidMoveException("Game is already over");
        }

        if (!validation(incomingGame, oldGame)) {
            throw new InvalidMoveException("Invalid move: previous cells were changed or move count is not exactly one");
        }

        Integer storedComputerMark = oldGame.getComputerMark();
        int computerMark;

        if (storedComputerMark != null) {
            computerMark = storedComputerMark;
            int playerMark = -computerMark;
            int[][] after = incomingGame.getGameField().getMatrix();
            int[][] before = oldGame.getGameField().getMatrix();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (before[i][j] == 0 && after[i][j] != 0 && after[i][j] != playerMark) {
                        throw new InvalidMoveException("Invalid move: wrong mark placed");
                    }
                }
            }
        } else {
            computerMark = getComputerMark(incomingGame, oldGame);
        }

        Game result = makeNextMove(incomingGame, computerMark);
        Game savedGame = new Game(result.getUuid(), result.getGameField(), computerMark);
        gameRepository.save(dataGameMapper.toDataSource(savedGame));
        return savedGame;
    }

    private int getComputerMark(Game incomingGame, Game oldGame) {
        int[][] after = incomingGame.getGameField().getMatrix();
        int[][] before = oldGame.getGameField().getMatrix();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (before[i][j] == 0 && after[i][j] != 0) {
                    return -after[i][j];
                }
            }
        }
        return 0;
    }

    private int[][] copyMatrix(int[][] matrix) {
        int[][] copy = new int[3][3];
        for (int i = 0; i < 3; i++) {
            copy[i] = matrix[i].clone();
        }
        return copy;
    }

    private GameResult getWinner(int[][] matrix) {
        for (int i = 0; i < 3; i++) {
            GameResult result = checkLine(matrix[i][0], matrix[i][1], matrix[i][2]);
            if (result != null) return result;
        }

        for (int j = 0; j < 3; j++) {
            GameResult result = checkLine(matrix[0][j], matrix[1][j], matrix[2][j]);
            if (result != null) return result;
        }

        GameResult diag1 = checkLine(matrix[0][0], matrix[1][1], matrix[2][2]);
        if (diag1 != null) return diag1;

        GameResult diag2 = checkLine(matrix[0][2], matrix[1][1], matrix[2][0]);
        if (diag2 != null) return diag2;

        for (int[] row : matrix) {
            for (int cell : row) {
                if (cell == 0) {
                    return GameResult.NOT_FINISHED;
                }
            }
        }

        return GameResult.DRAW;
    }

    private GameResult checkLine(int a, int b, int c) {
        int sum = a + b + c;
        if (sum == 3) return GameResult.X_WINS;
        if (sum == -3) return GameResult.O_WINS;
        return null;
    }
}
