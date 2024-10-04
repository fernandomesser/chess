package chess.MovesValidation;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PieceValidation implements Piece{

    private ChessGame.TeamColor teamColor;
    private ChessPosition position;
    private ChessPiece piece;
    public PieceValidation(ChessGame.TeamColor teamColor, ChessPosition position,ChessPiece piece){
        this.piece = piece;
        this.position = position;
        this.teamColor = teamColor;
    }

    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition startPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessMove> pieceMoves = allMoves(board,startPosition);
        for (ChessMove move:pieceMoves) {
            ChessBoard cloneBoard = board.cloneBoard();
            ChessBoard.move(cloneBoard, move, move.getPromotionPiece());

            if (!ChessGame.check(cloneBoard,getTeamColor())){
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    @Override
    public Collection<ChessMove> allMoves(ChessBoard board, ChessPosition myPosition) {
        return piece.pieceMoves(board,myPosition);
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public ChessPiece.PieceType getPieceType() {
        return piece.getPieceType();
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
