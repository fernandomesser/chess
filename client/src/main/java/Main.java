import chess.*;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import ui.ServerFacade;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        ServerFacade facade = new ServerFacade("http://localhost:8080/");
        try {
            UserData user = new UserData("joe","1234","jow@test.com");
            facade.register(user);
            AuthData auth = facade.logIn(user);
            System.out.println(auth);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }
}