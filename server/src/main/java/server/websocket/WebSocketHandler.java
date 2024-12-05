package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlGameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;


@WebSocket
public class WebSocketHandler {
    SqlAuthDAO authDAO = new SqlAuthDAO();
    SqlGameDAO gameDAO = new SqlGameDAO();


    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        try {
            UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
            switch (action.getCommandType()) {
                case CONNECT -> {
                    connect(action.getAuthToken(), action.getGameID(), session);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                    makeMove(makeMoveCommand.getGameID(), makeMoveCommand.getAuthToken(), makeMoveCommand.getMove(), session);
                }
                case LEAVE -> {
                    leave(action.getGameID(), action.getAuthToken(), session);
                }
                case RESIGN -> {
                    resign(action.getAuthToken(), action.getGameID(), session);
                }
            }
        } catch (Exception e) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Server Error")));
        }

    }

    private void connect(String auth, int gameID, Session session) throws Exception {
        String username = "";
        GameData gameData = null;
        try {
            gameData = gameDAO.getGame(gameID);
            AuthData authData = authDAO.getAuth(auth);
            username = authData.username();
        } catch (DataAccessException e) {
            throw new Exception("Failed to load game");
        }
        if (gameData == null) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Invalid Game Id")));
        } else {
            connections.add(gameID, auth, session);
            var message = "";
            if (gameData.blackUsername() != null && username.equals(gameData.blackUsername())) {
                message = String.format("%s joined the black team", username);
            } else if (gameData.whiteUsername() != null && username.equals(gameData.whiteUsername())) {
                message = String.format("%s joined the white team", username);
            } else {
                message = String.format("%s joined the game as an Observer", username);
            }

            var loadGameMessage = new LoadGameMessage(auth, gameData);
            session.getRemote().sendString(new Gson().toJson(loadGameMessage));
            connections.broadcast(gameID, auth, new NotificationMessage(message), false);
        }

    }

    public void makeMove(int gameID, String auth, ChessMove move, Session session) throws Exception {
        GameData gameData = null;
        String username = "";
        String message = "";
        ChessGame game = null;
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        try {
            gameData = gameDAO.getGame(gameID);
            game = gameData.game();
            AuthData authData = authDAO.getAuth(auth);
            username = authData.username();
        } catch (DataAccessException e) {
            throw new Exception("Failed to load game");
        }
        ChessPiece.PieceType movedPieceType;
        try {
            movedPieceType = gameData.game().getBoard().getPiece(move.getStartPosition()).getPieceType();

        }catch (Exception e){
            throw new Exception("Internal error");
        }

        String white = gameData.whiteUsername();
        String black = gameData.blackUsername();
        if (!(black != null && username.equals(black) || (white != null && username.equals(white)))) {
            message = new Gson().toJson(new ErrorMessage("You are observing the game"));
            session.getRemote().sendString(message);
        } else {
            try {
                canMove(username, gameData, move, game);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
            try {
                game.makeMove(move);
            }catch (Exception e){
                throw new Exception("Failed to make move");
            }
            gameDAO.updateGame(gameID, gameData);

            try {
                var moveNotification = new LoadGameMessage(auth, gameData);
                connections.broadcast(gameID, auth, moveNotification, true);
                message = String.format("%s moved %s from %s to %s", username, movedPieceType.toString(), start.toString(), end.toString());
                var notification = new NotificationMessage(message);
                connections.broadcast(gameID, auth, notification, false);
            } catch (Exception ex) {
                throw new Exception("Failed to send message");
            }
        }
        checkGameOver(gameData, gameID, auth);
    }

    private void checkGameOver(GameData game, int gameID, String auth) throws DataAccessException, IOException {
        String message = "";
        if (game.game().isInCheckmate(ChessGame.TeamColor.BLACK)) {
            message = String.format("%s has won. %s in Checkmate", game.whiteUsername(),game.blackUsername());
            var notification = new NotificationMessage(message);
            game.game().setGameOver(true);

            gameDAO.updateGame(gameID, game);
            connections.broadcast(gameID, auth, notification, true);
        } else if (game.game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
            message = String.format("%s has won. %s in Checkmate", game.blackUsername(),game.whiteUsername());
            var notification = new NotificationMessage(message);
            game.game().setGameOver(true);

            gameDAO.updateGame(gameID, game);
            connections.broadcast(gameID, auth, notification, true);
        } else if (game.game().isInStalemate(ChessGame.TeamColor.BLACK) || game.game().isInStalemate(ChessGame.TeamColor.WHITE)) {
            message = "Stalemate. Tie!";
            var notification = new NotificationMessage(message);
            game.game().setGameOver(true);

            gameDAO.updateGame(gameID, game);
            connections.broadcast(gameID, auth, notification, true);
        } else if (game.game().isInCheck(ChessGame.TeamColor.BLACK)) {
            var notification = new NotificationMessage("Black in check");
            connections.broadcast(gameID, auth, notification, true);
        } else if (game.game().isInCheck(ChessGame.TeamColor.WHITE)) {
            var notification = new NotificationMessage("White in check");
            connections.broadcast(gameID, auth, notification, true);
        }
    }

    private void canMove(String username, GameData gameData, ChessMove move, ChessGame game) throws Exception {
        String white = gameData.whiteUsername();
        String black = gameData.blackUsername();
        ChessGame.TeamColor turn = null;
        try {
            turn = game.getTeamTurn();
        } catch (Exception e) {
            throw new Exception("Error");
        }
        ChessGame.TeamColor userTeam = null;
        if (username.equals(black)) {
            userTeam = ChessGame.TeamColor.BLACK;
        } else if (username.equals(white)) {
            userTeam = ChessGame.TeamColor.WHITE;
        } else {
            throw new Exception("No player in the game");
        }

        Collection<ChessMove> validMoves = game.validMoves(move.getStartPosition());
        if (!validMoves.contains(move)) {
            throw new Exception("Invalid Move");
        }
        if (turn != null && !turn.equals(userTeam)) {
            throw new Exception("Not your turn to move");
        }
        if (game.isOver()) {
            throw new Exception("Game is over");
        }
    }


    private void resign(String auth, int gameID, Session session) throws Exception {
        GameData gameData = gameDAO.getGame(gameID);
        AuthData authData = authDAO.getAuth(auth);
        String username = authData.username();
        String black = gameData.blackUsername();
        String white = gameData.whiteUsername();
        String message = "";
        ChessGame game = gameData.game();

        if (!(black != null && username.equals(black) || (white != null && username.equals(white)))) {
            message = new Gson().toJson(new ErrorMessage("You are observing the game"));
            session.getRemote().sendString(message);
        } else {
            if (game.isOver()) {
                message = new Gson().toJson(new ErrorMessage("Game is Over"));
                session.getRemote().sendString(message);
            } else {
                gameData.game().setGameOver(true);
                if (username.equals(black)) {
                    game.setWinner(ChessGame.TeamColor.WHITE);
                    game.setWinnerName(white);
                } else if (username.equals(white)) {
                    game.setWinner(ChessGame.TeamColor.BLACK);
                    game.setWinnerName(black);
                } else {
                    throw new Exception("No player in the game");
                }
                gameDAO.updateGame(gameID, gameData);

                NotificationMessage notificationMessage = new NotificationMessage(String.format("%s has resigned", username));
                connections.broadcast(gameID, auth, notificationMessage, true);
            }

        }
    }

    private void leave(int gameID, String auth, Session session) throws IOException, SQLException, DataAccessException {
        AuthData authData = authDAO.getAuth(auth);
        GameData gameData = gameDAO.getGame(gameID);
        String username = authData.username();
        String white = gameData.whiteUsername();
        String black = gameData.blackUsername();
        if (username.equals(white)) {
            GameData updated = new GameData(gameID, null, black, gameData.gameName(), gameData.game());
            gameDAO.updateGame(gameID, updated);
        } else if (username.equals(black)) {
            GameData updated = new GameData(gameID, white, null, gameData.gameName(), gameData.game());
            gameDAO.updateGame(gameID, updated);
        }

        connections.remove(gameID, auth);
        var message = String.format("%s has left the Game", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, auth, notification, false);
    }

}