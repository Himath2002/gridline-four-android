package io.github.himath2002.gridlinefour.game;

/** Chooses a legal column for the computer-controlled second player. */
public interface ComputerMoveStrategy {
    int chooseColumn(GameEngine engine);
}
