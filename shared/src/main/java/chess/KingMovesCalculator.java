package chess;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import chess.ChessPosition;
import static chess.ChessPiece.PieceType.*;

public class KingMovesCalculator implements PieceMovesCalculator {

    private static final int[][] MOVES = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},           {0, 1},
        {1, -1},  {1, 0},  {1, 1}
    };
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        var row = myPosition.getRow();
        var col = myPosition.getColumn();

       for(int[] move: MOVES){
           int checkRow = row + move[0];
           int checkCol = col + move[1];
            ChessPosition position = new ChessPosition(checkRow,checkCol);
           if (inBound(checkRow, checkCol)){
            if (board.getPiece(position)==null||board.getPiece(position).getTeamColor() != board.getPiece(myPosition).getTeamColor()){
                validMoves.add(new ChessMove(myPosition, position, null));

            }
           }
       }
        return validMoves;
    }

    private boolean inBound(int checkRow, int checkCol) {
        return checkRow > 0 && checkRow < 9 && checkCol > 0 && checkCol < 9;
    }
}
