package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawBoard {
    private static final ChessGame game = new ChessGame();

//    public DrawBoard(ChessGame game) {
//        this.game = game;
//    }

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        printBoard(out);

    }

    private static void printBoard(PrintStream out) {
        for (int i = 0; i < 8; i++) {
            int i0 = i % 2 == 0 ? 1 : 0;
            int i1 = i % 2 == 0 ? 0 : 1;
            for (int j = 0; j < 8; j++) {
                if (j % 2 == i0) {
                    out.print(SET_BG_COLOR_BLACK);
                    printPiece(i + 1, j + 1, out);
                    out.print(RESET_BG_COLOR);
                }
                if (j % 2 == i1) {
                    out.print(SET_BG_COLOR_WHITE);
                    printPiece(i + 1, j + 1, out);
                    out.print(RESET_BG_COLOR);
                }
            }
            out.println();
        }
    }


    private static void printPiece(int i, int j, PrintStream out) {
        String piece = getPiece(i, j);
        switch (piece) {
            case "P" -> {
                out.print(SET_TEXT_COLOR_BLACK);
                System.out.print(BLACK_PAWN);
                out.print(RESET_TEXT_COLOR);
            }
            case "R" -> {
                out.print(SET_TEXT_COLOR_BLACK);
                System.out.print(BLACK_ROOK);
                out.print(RESET_TEXT_COLOR);
            }
            case "K" -> {
                out.print(SET_TEXT_COLOR_BLACK);
                System.out.print(BLACK_KING);
                out.print(RESET_TEXT_COLOR);
            }
            case "Q" -> {
                out.print(SET_TEXT_COLOR_BLACK);
                System.out.print(BLACK_QUEEN);
                out.print(RESET_TEXT_COLOR);
            }
            case "B" -> {
                out.print(SET_TEXT_COLOR_BLACK);
                System.out.print(BLACK_BISHOP);
                out.print(RESET_TEXT_COLOR);
            }
            case "N" -> {
                out.print(SET_TEXT_COLOR_BLACK);
                System.out.print(BLACK_KNIGHT);
                out.print(RESET_TEXT_COLOR);
            }
            case "p" -> {
                out.print(SET_TEXT_COLOR_WHITE);
                System.out.print(BLACK_PAWN);
                out.print(RESET_TEXT_COLOR);
            }
            case "r" -> {
                out.print(SET_TEXT_COLOR_WHITE);
                out.print(RESET_TEXT_COLOR);
                System.out.print(BLACK_ROOK);
            }
            case "k" -> {
                out.print(SET_TEXT_COLOR_WHITE);
                System.out.print(BLACK_KING);
                out.print(RESET_TEXT_COLOR);
            }
            case "q" -> {
                out.print(SET_TEXT_COLOR_WHITE);
                System.out.print(BLACK_QUEEN);
                out.print(RESET_TEXT_COLOR);
            }
            case "b" -> {
                out.print(SET_TEXT_COLOR_WHITE);
                System.out.print(BLACK_BISHOP);
                out.print(RESET_TEXT_COLOR);
            }
            case "n" -> {
                out.print(SET_TEXT_COLOR_WHITE);
                System.out.print(BLACK_KNIGHT);
                out.print(RESET_TEXT_COLOR);
            }
            default -> {
                System.out.print(EMPTY);
            }
        }
    }

    private static String getPiece(int i, int j) {
        ChessBoard board = game.getBoard();
        ChessPiece piece = board.getPiece(new ChessPosition(i, j));
        return piece == null ? " " : piece.toString();
    }

}
