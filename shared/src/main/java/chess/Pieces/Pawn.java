package chess.Pieces;

import chess.*;
import chess.MovesCalculator.PawnMovesCalculator;

import java.util.Collection;

public class Pawn implements Piece{

    private ChessGame.TeamColor teamColor;
    private ChessPosition position;
    public Pawn(ChessGame.TeamColor teamColor, ChessPosition position){
        this.position = position;
        this.teamColor = teamColor;
    }
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PawnMovesCalculator pawn = new PawnMovesCalculator();
        return pawn.pieceMoves(board, myPosition);
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public ChessPiece.PieceType getPieceType() {
        return ChessPiece.PieceType.PAWN;
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
