package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.setColor;

public class DrawBoard {
    private final ChessGame game;
    private final String view;
    private final Collection<ChessMove> moves;

    public DrawBoard(ChessGame game, String view, Collection<ChessMove> moves) {
        this.moves = moves;
        this.game = game;
        this.view = view;
        draw();
        System.out.println();
    }

    public void draw() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        printBoard(out);
    }


    private void printBoard(PrintStream out) {
        if (view.equals("BLACK")) {
            for (int i = 0; i < 10; i++) {
                for (int j = 9; j >= 0; j--) {
                    drawHeaders(out, i, j);
                    drawBoard(out, i, j, this.moves);
                }
                out.println();
            }
        } else {
            for (int i = 9; i >= 0; i--) {
                for (int j = 0; j < 10; j++) {
                    drawHeaders(out, i, j);
                    drawBoard(out, i, j, this.moves);
                }
                out.println();
            }
        }

    }


    private void printPiece(int i, int j, PrintStream out) {
        String whitePiece = setColor(true, 85, 90, 105);
        String blackPiece = setColor(true, 0, 0, 0);
        String piece = getPiece(i, j);
        switch (piece) {
            case "P" -> {
                pieceColor(out, BLACK_PAWN, whitePiece);
            }
            case "R" -> {
                pieceColor(out, BLACK_ROOK, whitePiece);
            }
            case "K" -> {
                pieceColor(out, BLACK_KING, whitePiece);
            }
            case "Q" -> {
                pieceColor(out, BLACK_QUEEN, whitePiece);
            }
            case "B" -> {
                pieceColor(out, BLACK_BISHOP, whitePiece);
            }
            case "N" -> {
                pieceColor(out, BLACK_KNIGHT, whitePiece);
            }
            case "p" -> {
                pieceColor(out, BLACK_PAWN, blackPiece);
            }
            case "r" -> {
                pieceColor(out, BLACK_ROOK, blackPiece);
            }
            case "k" -> {
                pieceColor(out, BLACK_KING, blackPiece);
            }
            case "q" -> {
                pieceColor(out, BLACK_QUEEN, blackPiece);
            }
            case "b" -> {
                pieceColor(out, BLACK_BISHOP, blackPiece);
            }
            case "n" -> {
                pieceColor(out, BLACK_KNIGHT, blackPiece);
            }
            default -> {
                System.out.print(EMPTY);
            }
        }
    }

    private String getPiece(int i, int j) {
        ChessBoard board = game.getBoard();
        ChessPiece piece = board.getPiece(new ChessPosition(i, j));
        return piece == null ? " " : piece.toString();
    }

    private void pieceColor(PrintStream out, String piece, String color) {
        out.print(color);
        System.out.print(piece);
        out.print(RESET_TEXT_COLOR);
    }

    private void highlight(PrintStream out, int i, int j, Collection<ChessMove> moves, boolean pattern) {
        for (ChessMove move : moves) {
            ChessPosition end = move.getEndPosition();
            if (end.getColumn() == j && end.getRow() == i) {
                out.print(setColor(false, 22, 38, 76));
                break;
            }
            if (!pattern) {
                out.print(setColor(false, 255, 255, 255));
            } else {
                out.print(setColor(false, 85, 0, 21));
            }


        }
    }

    private void drawBoard(PrintStream out, int i, int j, Collection<ChessMove> moves) {
        int i0 = i % 2 == 0 ? 1 : 0;
        int i1 = i % 2 == 0 ? 0 : 1;
        if (i < 9 && j < 9 && i > 0 && j > 0) {
            if (j % 2 == i0) {
                if (moves != null) {
                    if (!moves.isEmpty()){
                        highlight(out, i, j, moves, true);
                    }else {
                        out.print(setColor(false, 85, 0, 21));
                    }
                } else {
                    out.print(setColor(false, 85, 0, 21));
                }
                printPiece(i, j, out);
                out.print(RESET_BG_COLOR);
            }
            if (j % 2 == i1) {
                if (moves != null) {
                    if (!moves.isEmpty()){
                        highlight(out, i, j, moves, false);
                    }else {
                        out.print(setColor(false, 255, 255, 255));
                    }
                } else {
                    out.print(setColor(false, 255, 255, 255));
                }
                printPiece(i, j, out);
                out.print(RESET_BG_COLOR);
            }


        }
    }

    private void drawHeaders(PrintStream out, int i, int j) {
        String[] horizontal = {"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] vertical = {"1", "2", "3", "4", "5", "6", "7", "8"};
        out.print(setColor(false, 95, 94, 98));
        out.print(setColor(true, 0, 0, 0));

        if ((i == 0 || i == 9) || (j == 0 || j == 9)) {
            if ((j == 0 || j == 9) && i > 0 && i < 9) {
                System.out.print("\u2007\u2006" + vertical[i - 1] + "\u2007\u2004");
            } else if ((i == 0 || i == 9) && j > 0 && j < 9) {
                System.out.print("\u2007\u2006" + horizontal[j - 1] + "\u2007\u2004");
            } else {
                System.out.print(EMPTY);
            }
            out.print(RESET_BG_COLOR);
            out.print(RESET_TEXT_COLOR);
        }

    }

}
