package websocket.messages;

public class ErrorMessage extends ServerMessage{
    String message;
    public ErrorMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}{
}
