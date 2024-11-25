package server.websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

  private final ConnectionManager connections = new ConnectionManager();

  @OnWebSocketMessage
  public void onMessage(Session session, String message) throws IOException, ResponseException {
    UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
    switch (action.getCommandType()) {
      case CONNECT -> enter(action.getAuthToken(), session);
      case MAKE_MOVE -> makeMove(action.getAuthToken(), "T");
      case LEAVE -> leave(action.getAuthToken());
      case RESIGN -> resign(action.getAuthToken());
    }
  }

  private void resign(String authToken) {
  }

  private void enter(String visitorName, Session session) throws IOException {
    connections.add(visitorName, session);
    var message = String.format("%s is in the shop", visitorName);
    var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
    connections.broadcast(visitorName, notification);
  }

  private void leave(String visitorName) throws IOException {
    connections.remove(visitorName);
    var message = String.format("%s left the shop", visitorName);
    var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
    connections.broadcast(visitorName, notification);
  }

  public void makeMove(String petName, String sound) throws ResponseException {
    try {
      var message = String.format("%s says %s", petName, sound);
      var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
      connections.broadcast("", notification);
    } catch (Exception ex) {
      throw new ResponseException(500, ex.getMessage());
    }
  }
}