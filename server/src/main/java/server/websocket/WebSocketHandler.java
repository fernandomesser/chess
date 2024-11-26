package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SqlAuthDAO;
import exception.ResponseException;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
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
      case CONNECT -> join(action.getAuthToken(), session);
      case MAKE_MOVE -> makeMove(action.getAuthToken(), "T");
      case LEAVE -> leave(action.getAuthToken());
      case RESIGN -> resign(action.getAuthToken());
    }
  }

  private void join(String auth, Session session) throws IOException, SQLException, DataAccessException {
    connections.add(auth, session);
    AuthData authData = authDAO.getAuth(auth);
    var message = String.format("%s is in the shop", authData.username());
    var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
    connections.broadcast(auth, notification);
  }

  public void makeMove(String start, String end) throws ResponseException {
    try {
      var message = String.format("%s says %s", start, end);
      var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
      connections.broadcast("", notification);
    } catch (Exception ex) {
      throw new ResponseException(500, ex.getMessage());
    }
  }

  private void leave(String visitorName) throws IOException {
    connections.remove(visitorName);
    var message = String.format("%s left the shop", visitorName);
    var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
    connections.broadcast(visitorName, notification);
  }

  private void resign(String authToken) {
  }


}