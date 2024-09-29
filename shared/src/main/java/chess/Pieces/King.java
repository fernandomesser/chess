package chess.Pieces;

import chess.*;
import chess.MovesCalculator.KingMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class King implements Piece{

    private ChessGame.TeamColor teamColor;
    private ChessPosition position;
    private ChessPiece.PieceType type;
    public King(ChessGame.TeamColor teamColor, ChessPosition position){
        type = ChessPiece.PieceType.KING;
        this.teamColor = teamColor;
        this.position = position;
    }

    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition startPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessMove> kingMoves = pieceMoves(board,startPosition);
        for (ChessMove move:kingMoves) {
            ChessBoard cloneBoard = board;
            ChessBoard.move(cloneBoard, move);

            if (!ChessGame.check(cloneBoard,this.getTeamColor())){
                validMoves.add(move);
            }


        }
        return validMoves;
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
        return type;
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
