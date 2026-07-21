package io.github.himath2002.gridlinefour.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GameEngineTest {
    @Test
    public void dropDisc_appliesGravityAndAlternatesTurns() {
        GameEngine engine = new GameEngine(6, 7);

        MoveResult first = engine.dropDisc(3);
        MoveResult second = engine.dropDisc(3);

        assertTrue(first.isAccepted());
        assertEquals(5, first.getMove().getRow());
        assertEquals(Disc.PLAYER_ONE, engine.getCell(5, 3));
        assertEquals(Disc.PLAYER_TWO, engine.getCell(4, 3));
        assertEquals(Disc.PLAYER_ONE, second.getNextTurn());
    }

    @Test
    public void dropDisc_rejectsInvalidAndFullColumnsWithoutChangingTurn() {
        GameEngine engine = new GameEngine(4, 4);

        MoveResult invalid = engine.dropDisc(-1);
        for (int move = 0; move < 4; move++) {
            assertTrue(engine.dropDisc(0).isAccepted());
        }
        MoveResult full = engine.dropDisc(0);

        assertEquals(MoveResult.Rejection.INVALID_COLUMN, invalid.getRejection());
        assertEquals(MoveResult.Rejection.COLUMN_FULL, full.getRejection());
        assertEquals(Disc.PLAYER_ONE, engine.getCurrentTurn());
    }

    @Test
    public void dropDisc_detectsHorizontalWinFromLastMove() {
        GameEngine engine = new GameEngine(6, 7);

        play(engine, 0, 6, 1, 6, 2, 5, 3);

        assertEquals(GameStatus.PLAYER_ONE_WON, engine.getStatus());
        assertEquals(MoveResult.Rejection.GAME_COMPLETE, engine.dropDisc(4).getRejection());
    }

    @Test
    public void dropDisc_detectsVerticalWin() {
        GameEngine engine = new GameEngine(6, 7);

        play(engine, 0, 1, 0, 1, 0, 2, 0);

        assertEquals(GameStatus.PLAYER_ONE_WON, engine.getStatus());
    }

    @Test
    public void dropDisc_detectsBothDiagonalDirections() {
        GameEngine rising = new GameEngine(6, 7);
        play(rising, 0, 1, 1, 2, 6, 2, 2, 3, 6, 3, 5, 3, 3);
        assertEquals(GameStatus.PLAYER_ONE_WON, rising.getStatus());

        GameEngine falling = new GameEngine(6, 7);
        play(falling, 3, 2, 2, 1, 6, 1, 1, 0, 6, 0, 5, 0, 0);
        assertEquals(GameStatus.PLAYER_ONE_WON, falling.getStatus());
    }

    @Test
    public void undoLastMove_restoresCellTurnAndPlayableState() {
        GameEngine engine = new GameEngine(6, 7);
        play(engine, 0, 6, 1, 6, 2, 5, 3);

        Move undone = engine.undoLastMove();

        assertNotNull(undone);
        assertEquals(Disc.EMPTY, engine.getCell(5, 3));
        assertEquals(Disc.PLAYER_ONE, engine.getCurrentTurn());
        assertEquals(GameStatus.IN_PROGRESS, engine.getStatus());
        assertNull(new GameEngine(6, 7).undoLastMove());
    }

    @Test
    public void copyBoard_doesNotExposeEngineState() {
        GameEngine engine = new GameEngine(6, 7);
        engine.dropDisc(2);

        Disc[][] copy = engine.copyBoard();
        copy[5][2] = Disc.EMPTY;

        assertEquals(Disc.PLAYER_ONE, engine.getCell(5, 2));
        assertFalse(engine.getOpenColumns().isEmpty());
    }

    private static void play(GameEngine engine, int... columns) {
        for (int column : columns) {
            assertTrue("Expected column " + column + " to accept a move", engine.dropDisc(column).isAccepted());
        }
    }
}
