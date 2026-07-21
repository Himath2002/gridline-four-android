package io.github.himath2002.gridlinefour.game;

/** Outcome returned for every attempted move. */
public final class MoveResult {
    public enum Rejection {
        NONE,
        INVALID_COLUMN,
        COLUMN_FULL,
        GAME_COMPLETE
    }

    private final Move move;
    private final GameStatus status;
    private final Disc nextTurn;
    private final Rejection rejection;

    private MoveResult(Move move, GameStatus status, Disc nextTurn, Rejection rejection) {
        this.move = move;
        this.status = status;
        this.nextTurn = nextTurn;
        this.rejection = rejection;
    }

    static MoveResult accepted(Move move, GameStatus status, Disc nextTurn) {
        return new MoveResult(move, status, nextTurn, Rejection.NONE);
    }

    static MoveResult rejected(GameStatus status, Disc nextTurn, Rejection rejection) {
        return new MoveResult(null, status, nextTurn, rejection);
    }

    public boolean isAccepted() {
        return rejection == Rejection.NONE;
    }

    public Move getMove() {
        return move;
    }

    public GameStatus getStatus() {
        return status;
    }

    public Disc getNextTurn() {
        return nextTurn;
    }

    public Rejection getRejection() {
        return rejection;
    }
}
