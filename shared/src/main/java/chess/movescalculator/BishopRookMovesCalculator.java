package chess.movescalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;


public class BishopRookMovesCalculator implements PieceMovesCalculator {

    //All Possible Directions
    private static final int[][] BISHOP_MOVES = {
            {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
    };
    private static final int[][] ROOK_MOVES = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    //Returns a List of all possible moves for the piece
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece.PieceType type = board.getPiece(myPosition).getPieceType();
        int[][] moves = type.equals(ChessPiece.PieceType.BISHOP) ? BISHOP_MOVES : ROOK_MOVES;
        for (int[] move : moves) {
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

    //Check if Move is out of bounds
    private boolean inBound(int row, int col) {
        return (row > 0 && row < 9) && (col > 0 && col < 9);
    }
}
