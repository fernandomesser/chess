package chess.movescalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KingKnightMovesCalculator implements PieceMovesCalculator {

    //All Directions
    private static final int[][] KNIGHT_MOVES = {
            {1, -2}, {2, -1}, {2, 1},
            {1, 2}, {-1, 2}, {-2, 1},
            {-2, -1}, {-1, -2}
    };
    private static final int[][] KING_MOVES = {

            {1, -1}, {1, 0}, {1, 1},
            {0, -1}, {0, 1},
            {-1, -1}, {-1, 0}, {-1, 1}

    };

    //Returns a List of all possible moves for the piece
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece.PieceType type = board.getPiece(myPosition).getPieceType();
        int[][] moves = type.equals(ChessPiece.PieceType.KING) ? KING_MOVES : KNIGHT_MOVES;

        var row = myPosition.getRow();
        var col = myPosition.getColumn();

        for (int[] move : moves) {
            var checkRow = row + move[0];
            var checkCol = col + move[1];

            ChessPosition newPosition = new ChessPosition(checkRow, checkCol);
            ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
            if (inBound(checkRow, checkCol)) {
                if (board.getPiece(newPosition) == null || !board.getPiece(newPosition).getTeamColor().equals(color)) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }

        }
        return validMoves;
    }

    //Check if move is out of bounds
    private boolean inBound(int row, int col) {
        return (row > 0 && row < 9) && (col > 0 && col < 9);
    }
}
