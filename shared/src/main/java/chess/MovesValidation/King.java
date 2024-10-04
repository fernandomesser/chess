package chess.MovesValidation;

import chess.*;
import chess.MovesCalculator.KingMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class King implements Piece{

    private ChessGame.TeamColor teamColor;
    private ChessPosition position;
    public King(ChessGame.TeamColor teamColor, ChessPosition position){
        this.teamColor = teamColor;
        this.position = position;
    }

    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition startPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessMove> pieceMoves = allMoves(board,startPosition);
        for (ChessMove move:pieceMoves) {
            ChessBoard cloneBoard = board.cloneBoard();
            ChessBoard.move(cloneBoard, move, null);

            if (!ChessGame.check(cloneBoard,getTeamColor())){
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    @Override
    public Collection<ChessMove> allMoves(ChessBoard board, ChessPosition myPosition) {
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

    @Override
    public ChessPosition getPosition() {
        return position;
    }

    @Override
    public void setPosition(ChessPosition newPosition) {
        position = newPosition;
    }
}
