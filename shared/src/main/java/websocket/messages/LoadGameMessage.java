package websocket.messages;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.GameData;

public class LoadGameMessage extends ServerMessage{
    String auth;
    GameData game;
    String message;
    public LoadGameMessage(String auth, GameData game, String message) {
        super(ServerMessageType.LOAD_GAME);
        this.auth = auth;
        this.game = game;
        this.message = message;
    }

    public GameData getGame(){
        return game;
    }
    public String getMessage(){
        return message;
    }
    public String getAuth(){
        return auth;
    }
}
