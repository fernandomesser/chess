package chess.Pieces;

import chess.*;
import chess.MovesCalculator.BishopMovesCalculator;
import chess.MovesCalculator.PieceMovesCalculator;

import java.util.Collection;

public class Bishop implements Piece {

    private ChessGame.TeamColor teamColor;
    private ChessPosition position;
    public Bishop(ChessGame.TeamColor teamColor, ChessPosition position){
        this.teamColor = teamColor;
        this.position = position;
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

    @Override
    public ChessPosition getPosition() {
        return position;
    }

    @Override
    public void setPosition(ChessPosition newPosition) {
        position = newPosition;
    }
}
