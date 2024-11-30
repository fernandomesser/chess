package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
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
import java.sql.SQLException;
import java.util.Collection;


@WebSocket
public class WebSocketHandler {
    SqlAuthDAO authDAO = new SqlAuthDAO();
    SqlGameDAO gameDAO = new SqlGameDAO();


    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        try{
            UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
            switch (action.getCommandType()) {
                case CONNECT -> {
                    connect(action.getAuthToken(), action.getGameID(), session);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                    makeMove(makeMoveCommand.getGameID(), makeMoveCommand.getAuthToken(), makeMoveCommand.getMove());
                }
                case LEAVE -> leave(action.getGameID(), action.getAuthToken());
                case RESIGN -> resign(action.getAuthToken());
            }
        }catch (Exception e){
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
                System.out.println("White");
            } else if (gameData.whiteUsername() != null && username.equals(gameData.whiteUsername())) {
                System.out.println("Black");
                message = String.format("%s joined the white team", username);
            } else {
                System.out.println("Observer");
                message = String.format("%s joined the game as an Observer", username);
            }

            var loadGameMessage = new LoadGameMessage(auth, gameData);
            session.getRemote().sendString(new Gson().toJson(loadGameMessage));
            connections.broadcast(gameID, auth, new NotificationMessage(message));
        }

    }

    public void makeMove(int gameID, String auth, ChessMove move) throws Exception {
        GameData gameData = null;
        String username = "";
        try {
            gameData = gameDAO.getGame(gameID);
            AuthData authData = authDAO.getAuth(auth);
            username = authData.username();
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
        String white = gameData.whiteUsername();
        String black = gameData.blackUsername();
        ChessGame.TeamColor turn = gameData.game().getTeamTurn();
        ChessGame.TeamColor userTeam = null;
        if (username.equals(black)){
            userTeam = ChessGame.TeamColor.BLACK;
        }else if (username.equals(white)){
            userTeam = ChessGame.TeamColor.WHITE;
        }else {
            throw new Exception("No player in the game");
        }

        Collection<ChessMove> validMoves = gameData.game().validMoves(move.getStartPosition());
        if (!validMoves.contains(move)){
            throw new Exception("Invalid Move");
        }
        if (!turn.equals(userTeam)){
            throw new Exception("Not your turn to move");
        }
        //game over
        if ()

        try {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            connections.broadcast(gameID, auth, notification);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
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