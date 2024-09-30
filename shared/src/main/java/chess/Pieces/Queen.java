package chess.Pieces;

import chess.*;
import chess.MovesCalculator.KingMovesCalculator;
import chess.MovesCalculator.QueenMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class Queen implements Piece{

    private ChessGame.TeamColor teamColor;
    private ChessPosition position;
    public Queen(ChessGame.TeamColor teamColor, ChessPosition position){
        this.position=position;
        this.teamColor = teamColor;
    }

    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition startPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessMove> pieceMoves = pieceMoves(board,startPosition);
        for (ChessMove move:pieceMoves) {
            ChessBoard cloneBoard = board.cloneBoard();
            ChessBoard.move(cloneBoard, move);

            if (!ChessGame.check(cloneBoard,this.getTeamColor())){
                validMoves.add(move);
            }
        }
        return validMoves;
    }
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        QueenMovesCalculator Queen = new QueenMovesCalculator();
        return Queen.pieceMoves(board, myPosition);
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public ChessPiece.PieceType getPieceType() {
        return ChessPiece.PieceType.QUEEN;
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
