package ui;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;

public class InGameHelper {

    public static ChessMove moveValidation(String start, String end, ChessPiece.PieceType promotionPiece) throws ResponseException {
        char colStartChar = start.charAt(0);
        char colEndChar = end.charAt(0);
        int rowStart = Character.getNumericValue(start.charAt(1));
        int colStart = (colStartChar - 'a') + 1;
        int rowEnd= Character.getNumericValue(end.charAt(1));
        int colEnd = (colEndChar - 'a') + 1;
        if ((rowStart >= 1 && rowStart <= 8)&& (colStart >= 1 && colStart <= 8) && (rowEnd >= 1 && rowEnd <= 8) &&(colEnd >= 1 && colEnd <= 8)){
            throw new ResponseException(400,"Invalid Position.");
        }
        return new ChessMove(new ChessPosition(rowStart,colStart),new ChessPosition(rowEnd,colEnd), promotionPiece);
    }




}
