package io.github.himath2002.gridlinefour.game;

import java.util.Comparator;
import java.util.List;

/**
 * Small deterministic opponent: finish a winning line, block an immediate loss,
 * then prefer the most central open column.
 */
public final class TacticalComputerStrategy implements ComputerMoveStrategy {
    @Override
    public int chooseColumn(GameEngine engine) {
        List<Integer> openColumns = engine.getOpenColumns();
        if (openColumns.isEmpty()) {
            return -1;
        }

        Disc computerDisc = engine.getCurrentTurn();
        for (int column : openColumns) {
            if (engine.wouldWin(column, computerDisc)) {
                return column;
            }
        }
        for (int column : openColumns) {
            if (engine.wouldWin(column, computerDisc.opponent())) {
                return column;
            }
        }

        double center = (engine.getColumns() - 1) / 2.0;
        return openColumns.stream()
                .min(Comparator
                        .comparingDouble((Integer column) -> Math.abs(column - center))
                        .thenComparingInt(Integer::intValue))
                .orElse(-1);
    }
}
