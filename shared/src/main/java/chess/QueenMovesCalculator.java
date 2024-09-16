package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator{

    private static final int[][] MOVES = {
            {1, -1}, {1, 0}, {1, 1},
            {0, -1},         {0, 1},
            {-1, -1},{-1, 0},{-1, 1}
    };
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (int[] move: MOVES){
            
        }

        return validMoves;
    }
}
