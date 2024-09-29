package chess.Pieces;

import chess.*;
import chess.MovesCalculator.KingMovesCalculator;
import chess.MovesCalculator.RookMovesCalculator;

import java.util.Collection;

public class Rook implements Piece{

    private ChessGame.TeamColor teamColor;
    private ChessPosition position;
    public Rook(ChessGame.TeamColor teamColor, ChessPosition position){
        this.position = position;
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

    @Override
    public ChessPosition getPosition() {
        return position;
    }

    @Override
    public void setPosition(ChessPosition newPosition) {
        position=newPosition;
    }
}
