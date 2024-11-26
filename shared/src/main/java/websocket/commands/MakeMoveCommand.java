package websocket.commands;

import java.util.Objects;

public class MakeMoveCommand extends UserGameCommand {
    private final int row;
    private final int col;

    public MakeMoveCommand(String authToken, Integer gameID, int row, int col) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MakeMoveCommand that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), row, col);
    }
}
