package chess.MovesCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {

    //All Directions
    private static final int[][] MOVES = {
            {1, -2}, {2, -1}, {2, 1},
            {1, 2}, {-1, 2}, {-2, 1},
            {-2, -1}, {-1, -2}
    };

    //Returns a List of all possible moves for the piece
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        var row = myPosition.getRow();
        var col = myPosition.getColumn();

        for (int[] move : MOVES) {
            var checkRow = row + move[0];
            var checkCol = col + move[1];

            ChessPosition newPosition = new ChessPosition(checkRow, checkCol);
            if (inBound(checkRow, checkCol)) {
                if (board.getPiece(newPosition) == null || !board.getPiece(newPosition).getTeamColor().equals(board.getPiece(myPosition).getTeamColor())) {
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
