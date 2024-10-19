package service;

import dataaccess.*;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameServiceTest {
    private static final GameDAO gameDataAccess = new MemoryGameDAO();
    private static final AuthDAO authDataAccess = new MemoryAuthDAO();
    static final GameService service = new GameService(gameDataAccess, authDataAccess);

    @BeforeEach
    void clear() throws ResponseException {
        service.clear();
    }
    

    @Test
    void positiveCreateGame() {
    }

    @Test
    void negativeCreateGame() {
    }

    @Test
    void positiveListGames() {
    }

    @Test
    void negativeListGames() {
    }

    @Test
    void positiveJoinGame() {
    }

    @Test
    void negativeJoinGame() {
    }
}