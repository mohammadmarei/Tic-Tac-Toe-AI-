package ai;

import game.Board;

public interface EvaluatFunction {
    double evaluate(Board board, char maxPlayer);
}
