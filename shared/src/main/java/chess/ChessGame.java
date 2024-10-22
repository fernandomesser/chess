package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        TeamColor color = board.getPiece(startPosition).getTeamColor();
        MoveValidation moveValidation = new MoveValidation(color, startPosition, piece);

        return new ArrayList<>(moveValidation.validMoves(board, startPosition));
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        boolean moveValid = false;
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null || !piece.getTeamColor().equals(getTeamTurn())) {
            throw new InvalidMoveException();
        }
        //Validate the move
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        for (ChessMove moves : validMoves) {
            if (moves.equals(move)) {
                moveValid = true;
                break;
            }
        }
        if (moveValid) {
            ChessBoard.movePiece(board, move, move.getPromotionPiece());
            if (getTeamTurn().equals(TeamColor.BLACK)) {
                setTeamTurn(TeamColor.WHITE);
            } else {
                setTeamTurn(TeamColor.BLACK);
            }
        } else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return check(board, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (check(board, teamColor)) {
            return isGameOver(board, teamColor);
        } else {
            return false;
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStaleMate(TeamColor teamColor) {
        if (!check(board, teamColor)) {
            return isGameOver(board, teamColor);
        } else {
            return false;
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return turn == chessGame.turn && Objects.equals(getBoard(), chessGame.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, getBoard());
    }

    //Check if the board is in check
    public static boolean check(ChessBoard board, TeamColor teamColor) {
        // Find the King position
        ChessPosition kingPosition = findKingPosition(board, teamColor);

        // If the King is in danger, return true
        return isKingInCheck(board, kingPosition, teamColor);
    }

    // Helper method to find the King's position on the board
    private static ChessPosition findKingPosition(ChessBoard board, TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (isKing(piece, teamColor)) {
                    return position;
                }
            }
        }
        return null;
    }

    // Helper method to check if the King is in check
    private static boolean isKingInCheck(ChessBoard board, ChessPosition kingPosition, TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition opponentPosition = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(opponentPosition);
                if (isOpponentPiece(piece, teamColor)) {
                    Collection<ChessMove> movesList = piece.pieceMoves(board, opponentPosition);
                    if (isKingUnderAttack(movesList, kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Helper method to check if a piece is the King of the given team
    private static boolean isKing(ChessPiece piece, TeamColor teamColor) {
        return piece != null && piece.getTeamColor().equals(teamColor) && piece.getPieceType().equals(ChessPiece.PieceType.KING);
    }

    // Helper method to check if a piece belongs to the opponent
    private static boolean isOpponentPiece(ChessPiece piece, TeamColor teamColor) {
        return piece != null && !piece.getTeamColor().equals(teamColor);
    }

    // Helper method to check if the King is under attack
    private static boolean isKingUnderAttack(Collection<ChessMove> movesList, ChessPosition kingPosition) {
        for (ChessMove move : movesList) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    //Check if Game over. No valid moves to save King
    private static boolean isGameOver(ChessBoard board, TeamColor teamColor) {
        boolean mate = true;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor().equals(teamColor)) {
                    Collection<ChessMove> pieceMoves = piece.pieceMoves(board, position);
                    mate = avoidCheck(board, pieceMoves, teamColor);
                    break;
                }
            }
            if (!mate) {
                break;
            }
        }
        return mate;
    }

    //Check if Check can be avoided
    private static boolean avoidCheck(ChessBoard board, Collection<ChessMove> pieceMoves, TeamColor teamColor) {
        for (ChessMove pieceMove : pieceMoves) {
            ChessBoard cloneBoard = board.cloneBoard();
            ChessBoard.movePiece(cloneBoard, pieceMove, null);
            if (!check(cloneBoard, teamColor)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Game{" +
                "turn=" + turn.toString() +
                ", board=" + board.toString() +
                '}';
    }
}
