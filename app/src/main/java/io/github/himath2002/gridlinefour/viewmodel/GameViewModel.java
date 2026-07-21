package io.github.himath2002.gridlinefour.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.github.himath2002.gridlinefour.game.ComputerMoveStrategy;
import io.github.himath2002.gridlinefour.game.GameEngine;
import io.github.himath2002.gridlinefour.game.GameSnapshot;
import io.github.himath2002.gridlinefour.game.GameStatus;
import io.github.himath2002.gridlinefour.game.MoveResult;
import io.github.himath2002.gridlinefour.game.TacticalComputerStrategy;
import io.github.himath2002.gridlinefour.model.BoardSize;
import io.github.himath2002.gridlinefour.model.GameMode;

/** Bridges the pure rules engine to lifecycle-aware Android UI state. */
public final class GameViewModel extends ViewModel {
    private final MutableLiveData<GameSnapshot> snapshot = new MutableLiveData<>();
    private final ComputerMoveStrategy computerStrategy = new TacticalComputerStrategy();
    private GameEngine engine;
    private GameMode mode = GameMode.COMPUTER;
    private boolean outcomeConsumed;

    public LiveData<GameSnapshot> getSnapshot() {
        return snapshot;
    }

    public void startNewGame(BoardSize boardSize, GameMode gameMode) {
        engine = new GameEngine(boardSize.getRows(), boardSize.getColumns());
        mode = gameMode;
        outcomeConsumed = false;
        publish();
    }

    public MoveResult playColumn(int column) {
        ensureGameStarted();
        MoveResult humanResult = engine.dropDisc(column);
        if (humanResult.isAccepted()
                && mode == GameMode.COMPUTER
                && engine.getStatus() == GameStatus.IN_PROGRESS) {
            int computerColumn = computerStrategy.chooseColumn(engine);
            if (computerColumn >= 0) {
                engine.dropDisc(computerColumn);
            }
        }
        publish();
        return humanResult;
    }

    public boolean undoLastMove() {
        ensureGameStarted();
        if (mode == GameMode.COMPUTER || engine.undoLastMove() == null) {
            return false;
        }
        outcomeConsumed = false;
        publish();
        return true;
    }

    public void resetGame() {
        ensureGameStarted();
        engine.reset();
        outcomeConsumed = false;
        publish();
    }

    public GameStatus consumeCompletedOutcome() {
        ensureGameStarted();
        if (outcomeConsumed || engine.getStatus() == GameStatus.IN_PROGRESS) {
            return null;
        }
        outcomeConsumed = true;
        return engine.getStatus();
    }

    public boolean hasGame() {
        return engine != null;
    }

    private void publish() {
        snapshot.setValue(new GameSnapshot(engine));
    }

    private void ensureGameStarted() {
        if (engine == null) {
            throw new IllegalStateException("Start a game before using the board");
        }
    }
}
