package websocket.messages;

import model.GameData;

public class LoadGameMessage extends ServerMessage {
    String auth;
    GameData game;

    public LoadGameMessage(String auth, GameData game) {
        super(ServerMessageType.LOAD_GAME);
        this.auth = auth;
        this.game = game;
    }

    public GameData getGame() {
        return game;
    }

    public String getAuth() {
        return auth;
    }
}
