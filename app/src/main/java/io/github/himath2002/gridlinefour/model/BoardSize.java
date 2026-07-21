package io.github.himath2002.gridlinefour.model;

/** Supported game-board dimensions, expressed as rows by columns. */
public enum BoardSize {
    COMPACT(5, 6),
    CLASSIC(6, 7),
    EXPANDED(7, 8);

    private final int rows;
    private final int columns;

    BoardSize(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }
}
