package chess.Pieces;

import chess.*;
import chess.MovesCalculator.KingMovesCalculator;
import chess.MovesCalculator.RookMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class Rook implements Piece{

    private ChessGame.TeamColor teamColor;
    private ChessPosition position;
    public Rook(ChessGame.TeamColor teamColor, ChessPosition position){
        this.position = position;
        this.teamColor = teamColor;
    }

    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition startPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessMove> pieceMoves = pieceMoves(board,startPosition);
        for (ChessMove move:pieceMoves) {
            ChessBoard cloneBoard = board.cloneBoard();
            ChessBoard.move(cloneBoard, move);

            if (!ChessGame.check(cloneBoard,getTeamColor())&&!ChessGame.checkMate(cloneBoard,getTeamColor())&&!ChessGame.staleMate(cloneBoard,getTeamColor())){
                validMoves.add(move);
            }
        }
        return validMoves;
    }
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        RookMovesCalculator Rook = new RookMovesCalculator();
        return Rook.pieceMoves(board, myPosition);
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public ChessPiece.PieceType getPieceType() {
        return ChessPiece.PieceType.ROOK;
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
