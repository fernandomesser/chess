import chess.*;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import ui.Repl;
import ui.ServerFacade;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var serverUrl = "http://localhost:8080";

        new Repl(serverUrl).run();
    }
}