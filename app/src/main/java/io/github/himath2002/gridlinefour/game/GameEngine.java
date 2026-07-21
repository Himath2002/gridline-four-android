package io.github.himath2002.gridlinefour.game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Pure four-in-a-row rules engine. Android views render this state but never own
 * game rules, which keeps gravity, win detection, and undo independently testable.
 */
public final class GameEngine {
    private static final int CONNECT_LENGTH = 4;

    private final int rows;
    private final int columns;
    private final Disc[][] board;
    private final Deque<Move> history = new ArrayDeque<>();
    private Disc currentTurn = Disc.PLAYER_ONE;
    private GameStatus status = GameStatus.IN_PROGRESS;

    public GameEngine(int rows, int columns) {
        if (rows < CONNECT_LENGTH || columns < CONNECT_LENGTH) {
            throw new IllegalArgumentException("A board must be at least 4 by 4");
        }
        this.rows = rows;
        this.columns = columns;
        this.board = new Disc[rows][columns];
        clearBoard();
    }

    public MoveResult dropDisc(int column) {
        if (status != GameStatus.IN_PROGRESS) {
            return MoveResult.rejected(status, currentTurn, MoveResult.Rejection.GAME_COMPLETE);
        }
        if (column < 0 || column >= columns) {
            return MoveResult.rejected(status, currentTurn, MoveResult.Rejection.INVALID_COLUMN);
        }

        int row = findOpenRow(column);
        if (row < 0) {
            return MoveResult.rejected(status, currentTurn, MoveResult.Rejection.COLUMN_FULL);
        }

        Disc placedDisc = currentTurn;
        board[row][column] = placedDisc;
        Move move = new Move(row, column, placedDisc);
        history.addLast(move);

        if (hasConnectFour(row, column, placedDisc)) {
            status = placedDisc == Disc.PLAYER_ONE
                    ? GameStatus.PLAYER_ONE_WON
                    : GameStatus.PLAYER_TWO_WON;
        } else if (history.size() == rows * columns) {
            status = GameStatus.DRAW;
        } else {
            currentTurn = currentTurn.opponent();
        }

        return MoveResult.accepted(move, status, currentTurn);
    }

    public Move undoLastMove() {
        Move move = history.pollLast();
        if (move == null) {
            return null;
        }
        board[move.getRow()][move.getColumn()] = Disc.EMPTY;
        currentTurn = move.getDisc();
        status = GameStatus.IN_PROGRESS;
        return move;
    }

    public void reset() {
        history.clear();
        currentTurn = Disc.PLAYER_ONE;
        status = GameStatus.IN_PROGRESS;
        clearBoard();
    }

    public boolean canDrop(int column) {
        return status == GameStatus.IN_PROGRESS
                && column >= 0
                && column < columns
                && board[0][column] == Disc.EMPTY;
    }

    public boolean wouldWin(int column, Disc disc) {
        if (disc == null || disc == Disc.EMPTY || column < 0 || column >= columns) {
            return false;
        }
        int row = findOpenRow(column);
        if (row < 0) {
            return false;
        }
        board[row][column] = disc;
        boolean winningMove = hasConnectFour(row, column, disc);
        board[row][column] = Disc.EMPTY;
        return winningMove;
    }

    public List<Integer> getOpenColumns() {
        if (status != GameStatus.IN_PROGRESS) {
            return Collections.emptyList();
        }
        List<Integer> openColumns = new ArrayList<>();
        for (int column = 0; column < columns; column++) {
            if (canDrop(column)) {
                openColumns.add(column);
            }
        }
        return Collections.unmodifiableList(openColumns);
    }

    public Disc getCell(int row, int column) {
        if (row < 0 || row >= rows || column < 0 || column >= columns) {
            throw new IndexOutOfBoundsException("Cell is outside the board");
        }
        return board[row][column];
    }

    public Disc[][] copyBoard() {
        Disc[][] copy = new Disc[rows][columns];
        for (int row = 0; row < rows; row++) {
            System.arraycopy(board[row], 0, copy[row], 0, columns);
        }
        return copy;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getMoveCount() {
        return history.size();
    }

    public int getRemainingMoveCount() {
        return rows * columns - history.size();
    }

    public Disc getCurrentTurn() {
        return currentTurn;
    }

    public GameStatus getStatus() {
        return status;
    }

    private int findOpenRow(int column) {
        for (int row = rows - 1; row >= 0; row--) {
            if (board[row][column] == Disc.EMPTY) {
                return row;
            }
        }
        return -1;
    }

    private boolean hasConnectFour(int row, int column, Disc disc) {
        return connectedCount(row, column, disc, 1, 0) >= CONNECT_LENGTH
                || connectedCount(row, column, disc, 0, 1) >= CONNECT_LENGTH
                || connectedCount(row, column, disc, 1, 1) >= CONNECT_LENGTH
                || connectedCount(row, column, disc, 1, -1) >= CONNECT_LENGTH;
    }

    private int connectedCount(int row, int column, Disc disc, int rowStep, int columnStep) {
        return 1
                + countDirection(row, column, disc, rowStep, columnStep)
                + countDirection(row, column, disc, -rowStep, -columnStep);
    }

    private int countDirection(int row, int column, Disc disc, int rowStep, int columnStep) {
        int count = 0;
        int nextRow = row + rowStep;
        int nextColumn = column + columnStep;
        while (nextRow >= 0 && nextRow < rows
                && nextColumn >= 0 && nextColumn < columns
                && board[nextRow][nextColumn] == disc) {
            count++;
            nextRow += rowStep;
            nextColumn += columnStep;
        }
        return count;
    }

    private void clearBoard() {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                board[row][column] = Disc.EMPTY;
            }
        }
    }
}
