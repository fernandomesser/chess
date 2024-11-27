package server.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SqlAuthDAO;
import exception.ResponseException;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.sql.SQLException;


@WebSocket
public class WebSocketHandler {
  SqlAuthDAO authDAO = new SqlAuthDAO();


  private final ConnectionManager connections = new ConnectionManager();

  @OnWebSocketMessage
  public void onMessage(Session session, String message) throws IOException, ResponseException, SQLException, DataAccessException {
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
  }

  private void connect(String auth, int gameID, Session session) throws IOException, SQLException, DataAccessException {
    connections.add(gameID, auth, session);
    AuthData authData = authDAO.getAuth(auth);
    var message = String.format("%s is in the shop", authData.username());
    var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
    connections.broadcast(gameID, auth, notification);
  }

  public void makeMove(int gameID, String auth, ChessMove move) throws ResponseException {
    try {
      var message = String.format("");
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