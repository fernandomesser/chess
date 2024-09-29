package chess.Pieces;

import chess.*;
import chess.MovesCalculator.KingMovesCalculator;
import chess.MovesCalculator.RookMovesCalculator;

import java.util.Collection;

public class Rook implements Piece{

    private ChessGame.TeamColor teamColor;
    public Rook(ChessGame.TeamColor teamColor){
        this.teamColor = teamColor;
    }
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        RookMovesCalculator Rook = new RookMovesCalculator();
        return Rook.pieceMoves(board, myPosition);
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public ChessPiece.PieceType getPieceType() {
        return ChessPiece.PieceType.ROOK;
    }
}
