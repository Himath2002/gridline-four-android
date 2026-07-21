package io.github.himath2002.gridlinefour.model;

/** Immutable session statistics for one local profile. */
public final class PlayerStatistics {
    private final int wins;
    private final int losses;
    private final int draws;

    public PlayerStatistics() {
        this(0, 0, 0);
    }

    private PlayerStatistics(int wins, int losses, int draws) {
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
    }

    public PlayerStatistics withWin() {
        return new PlayerStatistics(wins + 1, losses, draws);
    }

    public PlayerStatistics withLoss() {
        return new PlayerStatistics(wins, losses + 1, draws);
    }

    public PlayerStatistics withDraw() {
        return new PlayerStatistics(wins, losses, draws + 1);
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getDraws() {
        return draws;
    }

    public int getGamesPlayed() {
        return wins + losses + draws;
    }

    public double getWinRate() {
        return getGamesPlayed() == 0 ? 0.0 : wins * 100.0 / getGamesPlayed();
    }
}
