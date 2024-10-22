package chess.movescalculator;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {

    //All Possible Directions
    private static final int[][] MOVES = {

            {1, -1}, {1, 0}, {1, 1},
            {0, -1}, {0, 1},
            {-1, -1}, {-1, 0}, {-1, 1}

    };

    //Returns a List of all possible moves for the piece
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        var row = myPosition.getRow();
        var col = myPosition.getColumn();

        for (int[] move : MOVES) {
            int checkRow = row + move[0];
            int checkCol = col + move[1];

            ChessPosition newPosition = new ChessPosition(checkRow, checkCol);
            ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
            if (inBound(newPosition.getRow(), newPosition.getColumn())) {
                if (board.getPiece(newPosition) == null || !board.getPiece(newPosition).getTeamColor().equals(color)) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
        return validMoves;
    }

    private boolean inBound(int row, int col) {
        return (row > 0 && row < 9) && (col > 0 && col < 9);
    }
}
