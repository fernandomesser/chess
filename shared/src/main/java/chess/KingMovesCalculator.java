package chess;
import java.util.ArrayList;
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

       


        return validMoves;
    }
}
