package websocket.messages;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class LoadGameMessage extends ServerMessage{
    String auth;
    String game;
    String message;
    public LoadGameMessage(String auth, ChessGame game, String message) {
        super(ServerMessageType.LOAD_GAME);
        this.auth = auth;
        this.game = new Gson().toJson(game);
        this.message = message;
    }

    public String getGame(){
        return game;
    }
    public String getMessage(){
        return message;
    }
    public String getAuth(){
        return auth;
    }
}
