package ai;

import game.Board;

public class ClassicalEval implements EvaluatFunction {

    @Override
    public double evaluate(Board board, char maxPlayer) {
        char winner = board.getWinner();
        char minPlayer = (maxPlayer == 'X') ? 'O' : 'X';

        if (winner == maxPlayer) return 1000;
        if (winner == minPlayer) return -1000;
        if (board.isFull()) return 0;

        double score = 0.0;

        if (board.getCell(1, 1) == maxPlayer) score += 3;
        if (board.getCell(1, 1) == minPlayer) score -= 3;

        int[][] corners = {{0,0}, {0,2}, {2,0}, {2,2}};
        for (int[] c : corners) {
            if (board.getCell(c[0], c[1]) == maxPlayer) score += 2;
            if (board.getCell(c[0], c[1]) == minPlayer) score -= 2;
        }

        score += lineHeuristic(board, maxPlayer, minPlayer);

        return score;
    }

    private double lineHeuristic(Board board, char maxPlayer, char minPlayer) {
        double s = 0.0;
        for (int r = 0; r < 3; r++) {
            s += lineScore(board.getCell(r,0), board.getCell(r,1), board.getCell(r,2), maxPlayer, minPlayer);
        }
        for (int c = 0; c < 3; c++) {
            s += lineScore(board.getCell(0,c), board.getCell(1,c), board.getCell(2,c), maxPlayer, minPlayer);
        }
        s += lineScore(board.getCell(0,0), board.getCell(1,1), board.getCell(2,2), maxPlayer, minPlayer);
        s += lineScore(board.getCell(0,2), board.getCell(1,1), board.getCell(2,0), maxPlayer, minPlayer);
        return s;
    }

    private double lineScore(char a, char b, char c, char maxPlayer, char minPlayer) {
        int maxCount = 0, minCount = 0, empty = 0;
        char[] arr = {a, b, c};
        for (char ch : arr) {
            if (ch == maxPlayer) maxCount++;
            else if (ch == minPlayer) minCount++;
            else empty++;
        }

        if (maxCount > 0 && minCount == 0) {
            if (maxCount == 2 && empty == 1) return 10;
            if (maxCount == 1 && empty == 2) return 3;
        }
        if (minCount > 0 && maxCount == 0) {
            if (minCount == 2 && empty == 1) return -9;
            if (minCount == 1 && empty == 2) return -2;
        }
        return 0;
    }
}
