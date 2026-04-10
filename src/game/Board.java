package game;

import java.util.*;

public class Board {

    private final char[][] grid = new char[3][3];

    public Board() {
        for (int i = 0; i < 3; i++) {
            Arrays.fill(grid[i], ' ');
        }
    }

    public char getCell(int r, int c) {
        return grid[r][c];
    }

    public void makeMove(int r, int c, char player) {
        grid[r][c] = player;
    }

    public Board copy() {
        Board b = new Board();
        for (int i = 0; i < 3; i++)
            System.arraycopy(this.grid[i], 0, b.grid[i], 0, 3);
        return b;
    }

    public List<Move> getLegalMoves() {
        List<Move> moves = new ArrayList<>();
        if (isTerminal()) return moves;
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (grid[r][c] == ' ')
                    moves.add(new Move(r, c));
        return moves;
    }

    public boolean isFull() {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (grid[r][c] == ' ') return false;
        return true;
    }

    public char getWinner() {
        for (int r = 0; r < 3; r++)
            if (grid[r][0] != ' ' && grid[r][0] == grid[r][1] && grid[r][1] == grid[r][2])
                return grid[r][0];

        for (int c = 0; c < 3; c++)
            if (grid[0][c] != ' ' && grid[0][c] == grid[1][c] && grid[1][c] == grid[2][c])
                return grid[0][c];

        if (grid[0][0] != ' ' && grid[0][0] == grid[1][1] && grid[1][1] == grid[2][2])
            return grid[0][0];

        if (grid[0][2] != ' ' && grid[0][2] == grid[1][1] && grid[1][1] == grid[2][0])
            return grid[0][2];

        return ' ';
    }

    public boolean isTerminal() {
        return getWinner() != ' ' || isFull();
    }
}
