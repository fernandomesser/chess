package chess.Pieces;

import chess.*;
import chess.MovesCalculator.KingMovesCalculator;
import chess.MovesCalculator.QueenMovesCalculator;

import java.util.Collection;

public class Queen implements Piece{

    private ChessGame.TeamColor teamColor;
    public Queen(ChessGame.TeamColor teamColor){
        this.teamColor = teamColor;
    }
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        QueenMovesCalculator Queen = new QueenMovesCalculator();
        return Queen.pieceMoves(board, myPosition);
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public ChessPiece.PieceType getPieceType() {
        return ChessPiece.PieceType.QUEEN;
    }
}