package io.github.himath2002.gridlinefour.game;

import java.util.Objects;

/** Immutable record of a successfully placed disc. */
public final class Move {
    private final int row;
    private final int column;
    private final Disc disc;

    public Move(int row, int column, Disc disc) {
        this.row = row;
        this.column = column;
        this.disc = Objects.requireNonNull(disc, "disc");
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Disc getDisc() {
        return disc;
    }
}
