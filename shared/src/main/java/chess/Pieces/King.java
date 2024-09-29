package chess.Pieces;

import chess.*;
import chess.MovesCalculator.KingMovesCalculator;

import java.util.Collection;

public class King implements Piece{

    private ChessGame.TeamColor teamColor;
    public King(ChessGame.TeamColor teamColor){
        this.teamColor = teamColor;
    }
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        KingMovesCalculator king = new KingMovesCalculator();
        return king.pieceMoves(board, myPosition);
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public ChessPiece.PieceType getPieceType() {
        return ChessPiece.PieceType.KING;
    }
}
