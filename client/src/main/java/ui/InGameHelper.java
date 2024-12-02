package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;

import java.util.Collection;
import java.util.Scanner;

public class InGameHelper {

    public static ChessMove moveValidation(String start, String end, ChessGame game, ChessGame.TeamColor playerColor, Scanner in) throws ResponseException {
        char[] colMap = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int colStart = 0;
        int colEnd = 0;
        for (int i = 0; i < colMap.length; i++){
            if (colMap[i] == start.charAt(0)){
                colStart = i+1;
            }
            if (colMap[i] == end.charAt(0)){
                colEnd = i+1;
            }
        }
        int rowStart = Character.getNumericValue(start.charAt(1));
        int rowEnd = Character.getNumericValue(end.charAt(1));
        ChessPosition startP = new ChessPosition(rowStart, colStart);
        ChessPosition endP = new ChessPosition(rowEnd, colEnd);
        ChessMove possibleMove = new ChessMove(startP, endP, null);
        Collection<ChessMove> moves = game.validMoves(startP);
        //Check if is promotion
        ChessPiece piece = game.getBoard().getPiece(startP);
        if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN) && (endP.getRow() == 8 || endP.getRow() == 1)) {
            ChessPiece.PieceType promotionPiece = getPromotion(in, playerColor);
            possibleMove.setPromotionPiece(promotionPiece);

        }
        if (moves.contains(possibleMove)) {
            return possibleMove;
        } else {
            System.out.print ("Ingame Helper  line 36 ");
            throw new ResponseException(400, "Invalid Move.");
        }
    }

    static ChessPiece.PieceType getPromotion(Scanner in, ChessGame.TeamColor color) throws ResponseException {
        System.out.println("What piece do you want to promote to?");
        System.out.println("""
                1. Queen
                2. Bishop
                3. Knight
                4. Rook""");
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
