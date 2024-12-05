package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;

import java.util.Collection;
import java.util.Scanner;

public class InGameHelper {

    public static ChessMove moveValidation(String strt, String end, ChessGame game, ChessGame.TeamColor color, Scanner in) throws Exception {
        char[] colMap = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int colStart = 0;
        int colEnd = 0;
        for (int i = 0; i < colMap.length; i++) {
            if (colMap[i] == strt.charAt(0)) {
                colStart = i + 1;
            }
            if (colMap[i] == end.charAt(0)) {
                colEnd = i + 1;
            }
        }
        int rowStart = Character.getNumericValue(strt.charAt(1));
        int rowEnd = Character.getNumericValue(end.charAt(1));
        ChessPosition startP = new ChessPosition(rowStart, colStart);
        ChessPosition endP = new ChessPosition(rowEnd, colEnd);
        ChessMove possibleMove = new ChessMove(startP, endP, null);
        Collection<ChessMove> moves;
        ChessPiece piece;
        try {
            moves = game.validMoves(startP);
            piece = game.getBoard().getPiece(startP);
        }catch (Exception e){
            throw new Exception("Please select a valid piece to move");
        }

        if (!game.getTeamTurn().equals(color)){
            throw new Exception("Not your turn to move");
        }
        if (!piece.getTeamColor().equals(color)) {
            throw new Exception("You can only move pieces on your team");
        }
        if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN) && (endP.getRow() == 8 || endP.getRow() == 1)) {
            ChessPiece.PieceType promotionPiece = getPromotion(in, color);
            possibleMove.setPromotionPiece(promotionPiece);

        }
        if (moves.contains(possibleMove)) {
            return possibleMove;
        } else {
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

    static Collection<ChessMove> possibleHighlight(String start, ChessGame game) {
        char[] colMap = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int colStart = 0;
        for (int i = 0; i < colMap.length; i++) {
            if (colMap[i] == start.charAt(0)) {
                colStart = i + 1;
            }
        }
        int rowStart = Character.getNumericValue(start.charAt(1));
        ChessPosition startP = new ChessPosition(rowStart, colStart);
        return game.validMoves(startP);

    }

}
