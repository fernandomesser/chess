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
    private static final String view = "WHITE";

//    public DrawBoard(ChessGame game) {
//        this.game = game;
//    }

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        printBoard(out);

    }

    private static void printBoard(PrintStream out) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                drawHeaders(out, i, j);
                drawBoard(out, i, j);
            }
            out.println();
        }
    }


    private static void printPiece(int i, int j, PrintStream out) {
        String textColor = view.equals("WHITE") ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK;
        String piece = getPiece(i, j);
        switch (piece) {
            case "P" -> {
                pieceColor(out, BLACK_PAWN, SET_TEXT_COLOR_BLACK);
            }
            case "R" -> {
                pieceColor(out, BLACK_ROOK, SET_TEXT_COLOR_BLACK);
            }
            case "K" -> {
                pieceColor(out, BLACK_KING, SET_TEXT_COLOR_BLACK);
            }
            case "Q" -> {
                pieceColor(out, BLACK_QUEEN, SET_TEXT_COLOR_BLACK);
            }
            case "B" -> {
                pieceColor(out, BLACK_BISHOP, SET_TEXT_COLOR_BLACK);
            }
            case "N" -> {
                pieceColor(out, BLACK_KNIGHT, SET_TEXT_COLOR_BLACK);
            }
            case "p" -> {
                pieceColor(out, BLACK_PAWN, SET_TEXT_COLOR_WHITE);
            }
            case "r" -> {
                pieceColor(out, BLACK_ROOK, SET_TEXT_COLOR_WHITE);
            }
            case "k" -> {
                pieceColor(out, BLACK_KING, SET_TEXT_COLOR_WHITE);
            }
            case "q" -> {
                pieceColor(out, BLACK_QUEEN, SET_TEXT_COLOR_WHITE);
            }
            case "b" -> {
                pieceColor(out, BLACK_BISHOP, SET_TEXT_COLOR_WHITE);
            }
            case "n" -> {
                pieceColor(out, BLACK_KNIGHT, SET_TEXT_COLOR_WHITE);
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

    private static void pieceColor(PrintStream out, String piece, String color) {
        out.print(color);
        System.out.print(piece);
        out.print(RESET_TEXT_COLOR);
    }


    private static void drawBoard(PrintStream out, int i, int j) {
        int i0 = i % 2 == 0 ? 1 : 0;
        int i1 = i % 2 == 0 ? 0 : 1;
        if (i < 9 && j < 9 && i > 0 && j > 0) {
            if (j % 2 == i0) {
                out.print(SET_BG_COLOR_RED);
                printPiece(i, j, out);
                out.print(RESET_BG_COLOR);
            }
            if (j % 2 == i1) {
                out.print(SET_BG_COLOR_LIGHT_GREY);
                printPiece(i, j, out);
                out.print(RESET_BG_COLOR);
            }
        }
    }

    private static void drawHeaders(PrintStream out, int i, int j) {
        if ((i == 0 || i == 9)||(j==0||j==9)) {
            out.print(SET_BG_COLOR_DARK_GREEN);
            System.out.print(EMPTY);
            out.print(RESET_BG_COLOR);
        }

    }

}
