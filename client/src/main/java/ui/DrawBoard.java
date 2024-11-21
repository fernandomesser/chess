package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.setColor;

public class DrawBoard {
    private final ChessGame game;
    private final String view;

    public DrawBoard(ChessGame game, String view) {
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
        if (view.equals("WHITE")) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    drawHeaders(out, i, j);
                    drawBoard(out, i, j);
                }
                out.println();
            }
        } else {
            for (int i = 9; i >= 0; i--) {
                for (int j = 9; j >= 0; j--) {
                    drawHeaders(out, i, j);
                    drawBoard(out, i, j);
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
                pieceColor(out, BLACK_PAWN, blackPiece);
            }
            case "R" -> {
                pieceColor(out, BLACK_ROOK, blackPiece);
            }
            case "K" -> {
                pieceColor(out, BLACK_KING, blackPiece);
            }
            case "Q" -> {
                pieceColor(out, BLACK_QUEEN, blackPiece);
            }
            case "B" -> {
                pieceColor(out, BLACK_BISHOP, blackPiece);
            }
            case "N" -> {
                pieceColor(out, BLACK_KNIGHT, blackPiece);
            }
            case "p" -> {
                pieceColor(out, BLACK_PAWN, whitePiece);
            }
            case "r" -> {
                pieceColor(out, BLACK_ROOK, whitePiece);
            }
            case "k" -> {
                pieceColor(out, BLACK_KING, whitePiece);
            }
            case "q" -> {
                pieceColor(out, BLACK_QUEEN, whitePiece);
            }
            case "b" -> {
                pieceColor(out, BLACK_BISHOP, whitePiece);
            }
            case "n" -> {
                pieceColor(out, BLACK_KNIGHT, whitePiece);
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


    private void drawBoard(PrintStream out, int i, int j) {
        int i0 = i % 2 == 0 ? 1 : 0;
        int i1 = i % 2 == 0 ? 0 : 1;
        if (i < 9 && j < 9 && i > 0 && j > 0) {
            if (j % 2 == i0) {
                out.print(setColor(false, 85, 0, 21));
                printPiece(i, j, out);
                out.print(RESET_BG_COLOR);
            }
            if (j % 2 == i1) {
                out.print(setColor(false, 255, 255, 255));
                printPiece(i, j, out);
                out.print(RESET_BG_COLOR);
            }
        }
    }

    private void drawHeaders(PrintStream out, int i, int j) {
        String[] horizontal = {"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] vertical = {"8", "7", "6", "5", "4", "3", "2", "1"};
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
