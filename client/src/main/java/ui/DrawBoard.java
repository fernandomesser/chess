package ui;

import static ui.EscapeSequences.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class DrawBoard {
    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        printBoard(out);

    }

    private static void printBoard(PrintStream out){
        for (int i = 0; i < 8; i++) {
            int i0 = i % 2 == 0 ? 1 : 0;
            int i1 = i % 2 == 0 ? 0 : 1;
            for (int j = 0; j < 8; j++) {
                if (j % 2 == i0) {
                    out.print(SET_BG_COLOR_BLACK);
                    System.out.print(EMPTY);
                    out.print(RESET_BG_COLOR);
                }
                if (j % 2 == i1) {
                    out.print(SET_BG_COLOR_WHITE);
                    System.out.print(EMPTY);
                    out.print(RESET_BG_COLOR);
                }
            }
            out.println();
        }
    }

}
