package chess.MovesCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator {

    //All possible directions
    private static final int[][] MOVES = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    //Returns a List of all possible moves for the piece
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (int[] move : MOVES) {
            var checkRow = myPosition.getRow();
            var checkCol = myPosition.getColumn();

            while (true) {
                checkRow += move[0];
                checkCol += move[1];

                if (!inBound(checkRow, checkCol)) {
                    break;
                }
                ChessPosition newPosition = new ChessPosition(checkRow, checkCol);

                if (board.getPiece(newPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                } else if (!board.getPiece(newPosition).getTeamColor().equals(board.getPiece(myPosition).getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                    break;
                } else {
                    break;
                }
                {

                }
            }

        }
        return validMoves;
    }

    //Checks if move is out of bounds
    private boolean inBound(int row, int col) {
        return (row > 0 && row < 9) && (col > 0 && col < 9);
    }
}
