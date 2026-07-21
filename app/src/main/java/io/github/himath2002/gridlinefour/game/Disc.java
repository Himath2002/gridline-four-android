package io.github.himath2002.gridlinefour.game;

/** Identifies the occupant of a board cell. */
public enum Disc {
    EMPTY,
    PLAYER_ONE,
    PLAYER_TWO;

    public Disc opponent() {
        if (this == PLAYER_ONE) {
            return PLAYER_TWO;
        }
        if (this == PLAYER_TWO) {
            return PLAYER_ONE;
        }
        return EMPTY;
    }
}
