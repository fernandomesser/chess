package chess.Pieces;

import chess.*;
import chess.MovesCalculator.BishopMovesCalculator;
import chess.MovesCalculator.PieceMovesCalculator;

import java.util.Collection;

public class Bishop implements Piece {

    private ChessGame.TeamColor teamColor;
    public Bishop(ChessGame.TeamColor teamColor){
        this.teamColor = teamColor;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        BishopMovesCalculator bishop = new BishopMovesCalculator();
        return bishop.pieceMoves(board, myPosition);
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public ChessPiece.PieceType getPieceType() {
        return ChessPiece.PieceType.BISHOP;
    }
}
