package chess;

import java.util.ArrayList;
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

        switch (type){
            case KING -> {
                KingMovesCalculator king = new KingMovesCalculator();
                return king.pieceMoves(board, myPosition);
            }
            case QUEEN -> {
                QueenMovesCalculator queen = new QueenMovesCalculator();
                return queen.pieceMoves(board, myPosition);

            }
            case BISHOP -> {
                BishopMovesCalculator bishop = new BishopMovesCalculator();
                return bishop.pieceMoves(board, myPosition);
            }
            case KNIGHT -> {
                KnightMovesCalculator knight = new KnightMovesCalculator();
                return knight.pieceMoves(board, myPosition);
            }
            case ROOK -> {
                RookMovesCalculator rook = new RookMovesCalculator();
                return rook.pieceMoves(board, myPosition);
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return type == that.type && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, pieceColor);
    }

    @Override
    public String toString() {
        switch (type){
            case KING -> {
                if (pieceColor.equals(ChessGame.TeamColor.WHITE)){
                    return "K";
                } else if (pieceColor.equals(ChessGame.TeamColor.BLACK)) {
                    return "k";
                }
                break;
            }
            case PAWN -> {
                if (pieceColor.equals(ChessGame.TeamColor.WHITE)){
                    return "P";
                } else if (pieceColor.equals(ChessGame.TeamColor.BLACK)) {
                    return "p";
                }
                break;
            }
            case ROOK -> {
                if (pieceColor.equals(ChessGame.TeamColor.WHITE)){
                    return "R";
                } else if (pieceColor.equals(ChessGame.TeamColor.BLACK)) {
                    return "r";
                }
                break;
            }
            case QUEEN -> {
                if (pieceColor.equals(ChessGame.TeamColor.WHITE)){
                    return "Q";
                } else if (pieceColor.equals(ChessGame.TeamColor.BLACK)) {
                    return "q";
                }
                break;
            }
            case BISHOP -> {
                if (pieceColor.equals(ChessGame.TeamColor.WHITE)){
                    return "B";
                } else if (pieceColor.equals(ChessGame.TeamColor.BLACK)) {
                    return "b";
                }
                break;
            }
            case KNIGHT -> {
                if (pieceColor.equals(ChessGame.TeamColor.WHITE)){
                    return "N";
                } else if (pieceColor.equals(ChessGame.TeamColor.BLACK)) {
                    return "n";
                }
                break;
            }
            default -> {
                return null;
            }

        }
        return null;
    }
}
