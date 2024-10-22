package chess.movescalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {

    //All possible directions based on conditions
    private static final int[][] M_WHITE = {{1, 0}};
    private static final int[][] M_START_WHITE = {{1, 0}, {2, 0}};
    private static final int[][] M_CAPTURE_WHITE = {{1, -1}, {1, 1}};

    private static final int[][] M_BLACK = {{-1, 0}};
    private static final int[][] M_START_BLACK = {{-1, 0}, {-2, 0}};
    private static final int[][] M_CAPTURE_BLACK = {{-1, -1}, {-1, 0}, {-1, 1}};
    private final ChessGame.TeamColor BLACK = ChessGame.TeamColor.BLACK;
    private final ChessGame.TeamColor WHITE = ChessGame.TeamColor.WHITE;

    //Returns a List of all possible moves for the piece
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        var row = myPosition.getRow();
        var col = myPosition.getColumn();

        //Get valid moves for White Pawn not in start position
        if (myPosition.getRow() != 2 && board.getPiece(myPosition).getTeamColor().equals(WHITE)) {
            notStartPosition(board, myPosition, validMoves, row, col, M_WHITE);
        }
        //Get valid moves for Black Pawn not in start position
        if (myPosition.getRow() != 7 && board.getPiece(myPosition).getTeamColor().equals(BLACK)) {
            notStartPosition(board, myPosition, validMoves, row, col, M_BLACK);
        }

        //Get valid moves for White Pawn in start position
        if (myPosition.getRow() == 2 && board.getPiece(myPosition).getTeamColor().equals(WHITE)) {
            startPosition(board, myPosition, validMoves, row, col, M_START_WHITE);
        }

        //Get valid moves for Black Pawn in start position
        if (myPosition.getRow() == 7 && board.getPiece(myPosition).getTeamColor().equals(BLACK)) {
            startPosition(board, myPosition, validMoves, row, col, M_START_BLACK);
        }


        //Calculate Capture Moves
        int[][] captureMoves = !board.getPiece(myPosition).getTeamColor().equals(BLACK) ? M_CAPTURE_WHITE : M_CAPTURE_BLACK;
        for (int[] move : captureMoves) {
            int checkRow = row + move[0];
            int checkCol = col + move[1];
            ChessPosition diagonal = new ChessPosition(checkRow, checkCol);

            if (inBound(diagonal) && board.getPiece(diagonal) != null) {
                if (!isPromotion(diagonal) && !board.getPiece(diagonal).getTeamColor().equals(board.getPiece(myPosition).getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, diagonal, null));
                }
                if (isPromotion(diagonal) && !board.getPiece(diagonal).getTeamColor().equals(board.getPiece(myPosition).getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, diagonal, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, diagonal, ChessPiece.PieceType.KNIGHT));
                    validMoves.add(new ChessMove(myPosition, diagonal, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(myPosition, diagonal, ChessPiece.PieceType.QUEEN));
                }
            }
        }
        return validMoves;
    }

    //Calculate moves of a Pawn not in start position
    private void notStartPosition(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves, int row, int col, int[][] moves) {
        for (int[] move : moves) {
            var checkRow = row + move[0];
            var checkCol = col + move[1];
            ChessPosition newPosition = new ChessPosition(checkRow, checkCol);
            if (inBound(newPosition)) {
                if (board.getPiece(newPosition) == null && !isPromotion(newPosition)) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                if (board.getPiece(newPosition) == null && isPromotion(newPosition)) {
                    validMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                    validMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                }

            }
        }
    }

    //Calculate moves of a Pawn in start position
    private void startPosition(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves, int row, int col, int[][] movesStart) {
        for (int[] move : movesStart) {
            int checkRow = row + move[0];
            int checkCol = col + move[1];
            ChessPosition position = new ChessPosition(checkRow, checkCol);

            if (board.getPiece(position) == null) {
                validMoves.add(new ChessMove(myPosition, position, null));
            } else {
                break;
            }
        }
    }


    //Check if move is out of bounds
    private boolean inBound(ChessPosition position) {
        return (position.getRow() > 0 && position.getRow() < 9) && (position.getColumn() > 0 && position.getColumn() < 9);
    }

    //Check if move is a Promotion move
    private boolean isPromotion(ChessPosition position) {
        return (position.getRow() == 8) || (position.getRow() == 1);
    }
}
