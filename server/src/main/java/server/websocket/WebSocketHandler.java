package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlGameDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import java.io.IOException;
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
                case LEAVE -> leave(action.getGameID(), action.getAuthToken());
                case RESIGN -> resign(action.getAuthToken());
            }
        } catch (Exception e) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage(e.getMessage())));
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
            throw new Exception(e.getMessage());
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
            connections.broadcast(gameID, auth, new NotificationMessage(message));
        }

    }

    public void makeMove(int gameID, String auth, ChessMove move, Session session) throws Exception {
        GameData gameData;
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
            throw new Exception(e.getMessage());
        }
        String white = gameData.whiteUsername();
        String black = gameData.blackUsername();
        if (!(black != null && username.equals(black) || (white != null && username.equals(white)))) {
            message = new Gson().toJson(new ErrorMessage("You are observing the game"));
            session.getRemote().sendString(message);
        } else {
            try {
                canMove(username, gameData, move, game);
                System.out.println("Test reach after");
            } catch (Exception e) {
                message = new Gson().toJson(new ErrorMessage(message));
                session.getRemote().sendString(message);
                return;
            }
            game.makeMove(move);
            gameDAO.updateGame(gameID, gameData);

            try {
                var moveNotification = new LoadGameMessage(auth, gameData);
                connections.broadcast(gameID, auth, moveNotification);
                ChessPiece.PieceType movedPieceType = gameData.game().getBoard().getPiece(move.getStartPosition()).getPieceType();
                message = String.format("%s moved %s from %s to %s", username, movedPieceType.toString(), start.toString(), end.toString());
                var notification = new NotificationMessage(message);
                connections.broadcast(gameID, auth, notification);
            } catch (Exception ex) {
                throw new ResponseException(500, ex.getMessage());
            }
        }


    }

    private void canMove(String username, GameData gameData, ChessMove move, ChessGame game) throws Exception {
        String white = gameData.whiteUsername();
        String black = gameData.blackUsername();
        ChessGame.TeamColor turn = null;
        try {
            turn = game.getTeamTurn();
        }catch (Exception e){
            System.out.println(e.getMessage());
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
        if (turn!= null &&!turn.equals(userTeam)) {
            throw new Exception("Not your turn to move");
        }
        if (gameOver(game, turn)) {
            throw new Exception("Game is over");
        }
    }

    private boolean gameOver(ChessGame game, ChessGame.TeamColor color) {
        return game.isInCheckmate(color) || game.isInStalemate(color);
    }

    private void leave(int gameID, String auth) throws IOException {
        connections.remove(gameID, auth);
        var message = String.format("%s left the shop", auth);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast(gameID, auth, notification);
    }

    private void resign(String authToken) {
    }


}