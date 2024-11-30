package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;

import java.util.Collection;
import java.util.Scanner;

public class InGameHelper {

    public static ChessMove moveValidation(String start, String end, ChessPiece.PieceType promotionPiece, ChessGame game, ChessGame.TeamColor playerColor) throws ResponseException {
        char colStartChar = start.charAt(0);
        char colEndChar = end.charAt(0);
        int rowStart = Character.getNumericValue(start.charAt(1));
        int colStart = (colStartChar - 'a') + 1;
        int rowEnd = Character.getNumericValue(end.charAt(1));
        int colEnd = (colEndChar - 'a') + 1;
        ChessPosition startP = new ChessPosition(rowStart, colStart);
        ChessPosition endP = new ChessPosition(rowEnd, colEnd);
        ChessMove possibleMove = new ChessMove(startP, endP, promotionPiece);
        Collection<ChessMove> moves = game.validMoves(startP);

        if (moves.contains(possibleMove)) {
            return possibleMove;
        } else {
            throw new ResponseException(400, "Invalid Move.");
        }
    }

    static ChessPiece.PieceType getPromotion(Scanner in, ChessGame.TeamColor color) throws ResponseException {
        System.out.println("What piece do you want to promote to?");
        System.out.println("1. Queen" +
                "2. Bishop" +
                "3. Knight" +
                "4. Rook");
        int promo;
        try {
            promo = in.nextInt();
        } catch (Exception e) {
            throw new ResponseException(400, "Please provide a valid number");
        }
        switch (promo) {
            case 1 -> {
                return ChessPiece.PieceType.QUEEN;
            }
            case 2 -> {
                return ChessPiece.PieceType.BISHOP;
            }
            case 3 -> {
                return ChessPiece.PieceType.KNIGHT;
            }
            case 4 -> {
                return ChessPiece.PieceType.ROOK;
            }
            default -> throw new ResponseException(400, "Please provide a valid number");
        }
    }

}
