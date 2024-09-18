package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator{
    private static final int[][] MOVES_WHITE ={{1, 0}};
    private static final int[][] MOVES_START_WHITE ={{1, 0},{2, 0}};
    private static final int[][] MOVES_CAPTURE_WHITE ={{1, -1},{1,0},{1, 1}};

    private static final int[] MOVES_BLACK ={-1, 0};
    private static final int[][] MOVES_START_BLACK ={{-1, 0},{-2, 0}};
    private static final int[][] MOVES_CAPTURE_BLACK ={{-1, -1},{-1,0},{-1, 1}};
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        var row = myPosition.getRow();
        var col = myPosition.getColumn();

        if (myPosition.getRow() != 2){
            for (int[] move: MOVES_WHITE){
                var checkRow = row + move[0];
                var checkCol = col + move[1];
                ChessPosition newPosition = new ChessPosition(checkRow, checkCol);
                ChessPosition diagonalL = new ChessPosition((checkRow+1),(checkCol-1));
                ChessPosition diagonalR = new ChessPosition(checkRow+1,checkCol-1);

                if (board.getPiece(newPosition)==null){
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                if (inBound(diagonalL.getRow(),diagonalL.getColumn())){
                    if (board.getPiece(diagonalL)!=null){
                        if (board.getPiece(diagonalL).getTeamColor().equals(board.getPiece(myPosition).getTeamColor())){
                            validMoves.add(new ChessMove(myPosition, diagonalL, null));
                        }
                    }
                }



            }
        }
        return validMoves;
    }


    private boolean inBound(int row, int col) {
        return (row > 0 && row < 9) && (col > 0 && col < 9);
    }
}
