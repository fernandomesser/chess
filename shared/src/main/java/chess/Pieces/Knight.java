package chess.Pieces;

import chess.*;
import chess.MovesCalculator.KingMovesCalculator;
import chess.MovesCalculator.KnightMovesCalculator;

import java.util.Collection;

public class Knight implements Piece{

    private ChessGame.TeamColor teamColor;
    public Knight(ChessGame.TeamColor teamColor){
        this.teamColor = teamColor;
    }
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        KnightMovesCalculator Knight = new KnightMovesCalculator();
        return Knight.pieceMoves(board, myPosition);
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public ChessPiece.PieceType getPieceType() {
        return ChessPiece.PieceType.KNIGHT;
    }
}
