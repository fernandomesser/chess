package chess.Pieces;

import chess.*;

import java.util.Collection;

public interface Piece {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
    public ChessGame.TeamColor getTeamColor();
    public ChessPiece.PieceType getPieceType();

}
