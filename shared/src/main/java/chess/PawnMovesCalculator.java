package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator{
    private static final int[][] MOVES_WHITE ={{1, 0}};
    private static final int[][] MOVES_START_WHITE ={{1, 0},{2, 0}};
    private static final int[][] MOVES_CAPTURE_WHITE ={{1, -1},{1, 1}};

    private static final int[][] MOVES_BLACK ={{-1, 0}};
    private static final int[][] MOVES_START_BLACK ={{-1, 0},{-2, 0}};
    private static final int[][] MOVES_CAPTURE_BLACK ={{-1, -1},{-1,0},{-1, 1}};
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        var row = myPosition.getRow();
        var col = myPosition.getColumn();

        if (myPosition.getRow() != 2 && board.getPiece(myPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)){
            movePiece(board, myPosition, validMoves, row, col, MOVES_WHITE);
        }
        if (myPosition.getRow() != 7 && board.getPiece(myPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)){
            movePiece(board, myPosition, validMoves, row, col, MOVES_BLACK);
        }

        if (myPosition.getRow() == 2 && board.getPiece(myPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)){
            startMove(board, myPosition, validMoves, row, col, MOVES_START_WHITE);
        }

        if (myPosition.getRow() == 7 && board.getPiece(myPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)){
            startMove(board, myPosition, validMoves, row, col, MOVES_START_BLACK);
        }





        int[][] captureMoves = !board.getPiece(myPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK) ? MOVES_CAPTURE_WHITE : MOVES_CAPTURE_BLACK;
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

    private void startMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves, int row, int col, int[][] movesStart) {
        for (int[] move: movesStart){
            int checkRow = row + move[0];
            int checkCol = col + move[1];
            ChessPosition position = new ChessPosition(checkRow, checkCol);

            if (board.getPiece(position) == null){
                validMoves.add(new ChessMove(myPosition, position, null));
            }else {
                break;
            }
        }
    }

    private void movePiece(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves, int row, int col, int[][] moves) {
        for (int[] move: moves){
            var checkRow = row + move[0];
            var checkCol = col + move[1];
            ChessPosition newPosition = new ChessPosition(checkRow, checkCol);

            if (board.getPiece(newPosition) == null && !isPromotion(newPosition)){
                validMoves.add(new ChessMove(myPosition, newPosition, null));
            }
            if (board.getPiece(newPosition) == null && isPromotion(newPosition)){
                validMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                validMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                validMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                validMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
            }
        }
    }


    private boolean inBound(ChessPosition position) {
        return (position.getRow() > 0 && position.getRow() < 9) && (position.getColumn() > 0 && position.getColumn() < 9);
    }
    private boolean isPromotion(ChessPosition position){
        return (position.getRow() == 8) || (position.getRow() == 1);
    }
}
