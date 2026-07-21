package io.github.himath2002.gridlinefour.game;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TacticalComputerStrategyTest {
    private final TacticalComputerStrategy strategy = new TacticalComputerStrategy();

    @Test
    public void chooseColumn_finishesComputerWinBeforeBlocking() {
        GameEngine engine = new GameEngine(6, 7);
        play(engine, 6, 0, 6, 1, 5, 2, 4);

        assertEquals(3, strategy.chooseColumn(engine));
    }

    @Test
    public void chooseColumn_blocksImmediateOpponentWin() {
        GameEngine engine = new GameEngine(6, 7);
        play(engine, 0, 6, 1, 6, 2);

        assertEquals(3, strategy.chooseColumn(engine));
    }

    @Test
    public void chooseColumn_prefersCenterWhenNoTacticIsRequired() {
        GameEngine engine = new GameEngine(6, 7);
        engine.dropDisc(0);

        assertEquals(3, strategy.chooseColumn(engine));
    }

    private static void play(GameEngine engine, int... columns) {
        for (int column : columns) {
            engine.dropDisc(column);
        }
    }
}
