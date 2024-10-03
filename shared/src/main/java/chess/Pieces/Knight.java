package chess.Pieces;

import chess.*;
import chess.MovesCalculator.KingMovesCalculator;
import chess.MovesCalculator.KnightMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class Knight implements Piece{

    private ChessGame.TeamColor teamColor;
    private ChessPosition position;
    public Knight(ChessGame.TeamColor teamColor, ChessPosition position){
        this.teamColor = teamColor;
        this.position = position;
    }

    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition startPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessMove> pieceMoves = pieceMoves(board,startPosition);
        for (ChessMove move:pieceMoves) {
            ChessBoard cloneBoard = board.cloneBoard();
            ChessBoard.move(cloneBoard, move, null);

            if (!ChessGame.check(cloneBoard,getTeamColor())&&!ChessGame.checkMate(cloneBoard,getTeamColor())&&!ChessGame.staleMate(cloneBoard,getTeamColor())){
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        KnightMovesCalculator Knight = new KnightMovesCalculator();
        return Knight.pieceMoves(board, myPosition);
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public ChessPiece.PieceType getPieceType() {
        return ChessPiece.PieceType.KNIGHT;
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
