package ai;

import game.Board;
import game.Difficulty;
import game.Move;

import java.util.List;
import java.util.Random;

public class AlphaBeta {

    private final EvaluatFunction eval;
    private final int maxDepth;
    private final Difficulty difficulty;
    private final Random rnd = new Random();
    private final double randomChance;

    public AlphaBeta(EvaluatFunction eval, Difficulty difficulty) {
        this.eval = eval;
        this.difficulty = difficulty;

        switch (difficulty) {
            case EASY:
                this.maxDepth = 1;
                this.randomChance = 0.50;
                break;
            case NORMAL:
                this.maxDepth = 2;
                this.randomChance = 0.25;
                break;
            default:
                this.maxDepth = 7;
                this.randomChance = 0.0;
                break;
        }
    }

    private char other(char p) {
        return (p == 'X') ? 'O' : 'X';
    }

    public Move findBestMove(Board board, char aiPlayer) {
        List<Move> moves = board.getLegalMoves();
        if (moves.isEmpty()) return null;

        if (rnd.nextDouble() < randomChance) {
            return moves.get(rnd.nextInt(moves.size()));
        }

        double bestValue = Double.NEGATIVE_INFINITY;
        Move bestMove = null;

        for (Move m : moves) {
            Board copy = board.copy();
            copy.makeMove(m.row, m.col, aiPlayer);

            double value = alphaBeta(copy, maxDepth - 1,
                    Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                    false, aiPlayer);

            if (bestMove == null || value > bestValue) {
                bestValue = value;
                bestMove = m;
            }
        }

        System.out.println("AI chose move " +
                "(" + bestMove.row + "," + bestMove.col + ")" +
                " with score = " + bestValue);

        return bestMove;
    }


    private double alphaBeta(Board board, int depth, double alpha, double beta,
                             boolean maximizing, char aiPlayer) {

        if (depth == 0 || board.isTerminal()) {
            return eval.evaluate(board, aiPlayer);
        }

        char current = maximizing ? aiPlayer : other(aiPlayer);
        List<Move> moves = board.getLegalMoves();

        if (maximizing) {
            double best = Double.NEGATIVE_INFINITY;
            for (Move m : moves) {
                Board copy = board.copy();
                copy.makeMove(m.row, m.col, current);

                double val = alphaBeta(copy, depth - 1, alpha, beta, false, aiPlayer);
                best = Math.max(best, val);
                alpha = Math.max(alpha, best);
                if (beta <= alpha) break;
            }
            return best;
        } else {
            double best = Double.POSITIVE_INFINITY;
            for (Move m : moves) {
                Board copy = board.copy();
                copy.makeMove(m.row, m.col, current);

                double val = alphaBeta(copy, depth - 1, alpha, beta, true, aiPlayer);
                best = Math.min(best, val);
                beta = Math.min(beta, best);
                if (beta <= alpha) break;
            }
            return best;
        }
    }
}
