package chess.MovesValidation;

import chess.*;

import java.util.Collection;

public interface Piece {
    public Collection<ChessMove> allMoves(ChessBoard board, ChessPosition myPosition);
    public ChessGame.TeamColor getTeamColor();
    public ChessPiece.PieceType getPieceType();

    public ChessPosition getPosition();
    public void setPosition(ChessPosition newPosition);

}
