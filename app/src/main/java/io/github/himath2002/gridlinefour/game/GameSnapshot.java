package io.github.himath2002.gridlinefour.game;

/** Immutable state published by the game ViewModel for rendering. */
public final class GameSnapshot {
    private final Disc[][] board;
    private final Disc currentTurn;
    private final GameStatus status;
    private final int moveCount;
    private final int remainingMoves;

    public GameSnapshot(GameEngine engine) {
        board = engine.copyBoard();
        currentTurn = engine.getCurrentTurn();
        status = engine.getStatus();
        moveCount = engine.getMoveCount();
        remainingMoves = engine.getRemainingMoveCount();
    }

    public Disc[][] getBoard() {
        Disc[][] copy = new Disc[board.length][];
        for (int row = 0; row < board.length; row++) {
            copy[row] = board[row].clone();
        }
        return copy;
    }

    public int getRows() {
        return board.length;
    }

    public int getColumns() {
        return board[0].length;
    }

    public Disc getCurrentTurn() {
        return currentTurn;
    }

    public GameStatus getStatus() {
        return status;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public int getRemainingMoves() {
        return remainingMoves;
    }
}
