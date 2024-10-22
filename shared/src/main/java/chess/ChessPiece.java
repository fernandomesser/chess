package chess;

import chess.movescalculator.*;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final PieceType type;
    private final ChessGame.TeamColor pieceColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.pieceColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceType type = getPieceType();

        switch (type) {
            case KING, KNIGHT -> {
                KingKnightMovesCalculator kingKnight = new KingKnightMovesCalculator();
                return kingKnight.pieceMoves(board, myPosition);
            }
            case QUEEN -> {
                QueenMovesCalculator queen = new QueenMovesCalculator();
                return queen.pieceMoves(board, myPosition);
            }
            case BISHOP, ROOK -> {
                BishopRookMovesCalculator bishopRook = new BishopRookMovesCalculator();
                return bishopRook.pieceMoves(board, myPosition);
            }
            case PAWN -> {
                PawnMovesCalculator pawn = new PawnMovesCalculator();
                return pawn.pieceMoves(board, myPosition);
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return type == that.type && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, pieceColor);
    }

    @Override
    public String toString() {
        switch (type) {
            case KING -> {
                return pieceColor.equals(ChessGame.TeamColor.WHITE) ? "K" : "k";
            }
            case PAWN -> {
                return pieceColor.equals(ChessGame.TeamColor.WHITE) ? "P" : "p";
            }
            case ROOK -> {
                return pieceColor.equals(ChessGame.TeamColor.WHITE) ? "R" : "r";
            }
            case QUEEN -> {
                return pieceColor.equals(ChessGame.TeamColor.WHITE) ? "Q" : "q";
            }
            case BISHOP -> {
                return pieceColor.equals(ChessGame.TeamColor.WHITE) ? "B" : "b";
            }
            case KNIGHT -> {
                return pieceColor.equals(ChessGame.TeamColor.WHITE) ? "N" : "n";
            }
            default -> {
                return null;
            }
        }
    }
}
