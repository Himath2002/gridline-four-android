package io.github.himath2002.gridlinefour.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.github.himath2002.gridlinefour.game.GameStatus;
import io.github.himath2002.gridlinefour.model.BoardSize;
import io.github.himath2002.gridlinefour.model.DiscPalette;
import io.github.himath2002.gridlinefour.model.GameMode;
import io.github.himath2002.gridlinefour.model.PlayerProfile;
import io.github.himath2002.gridlinefour.model.PlayerStatistics;
import io.github.himath2002.gridlinefour.navigation.AppScreen;

/** Owns navigation, local profiles, setup choices, and session-level statistics. */
public final class SessionViewModel extends ViewModel {
    private final MutableLiveData<AppScreen> screen = new MutableLiveData<>(AppScreen.HOME);
    private final MutableLiveData<PlayerProfile> playerOne = new MutableLiveData<>();
    private final MutableLiveData<PlayerProfile> playerTwo = new MutableLiveData<>();
    private final MutableLiveData<PlayerStatistics> playerOneStats =
            new MutableLiveData<>(new PlayerStatistics());
    private final MutableLiveData<PlayerStatistics> playerTwoStats =
            new MutableLiveData<>(new PlayerStatistics());
    private final MutableLiveData<BoardSize> boardSize = new MutableLiveData<>(BoardSize.CLASSIC);
    private final MutableLiveData<GameMode> gameMode = new MutableLiveData<>(GameMode.COMPUTER);
    private final MutableLiveData<DiscPalette> discPalette =
            new MutableLiveData<>(DiscPalette.CORAL_AND_GOLD);
    private int profileBeingEdited = 1;

    public LiveData<AppScreen> getScreen() {
        return screen;
    }

    public void navigateTo(AppScreen destination) {
        screen.setValue(destination);
    }

    public void editProfile(int playerNumber) {
        if (playerNumber != 1 && playerNumber != 2) {
            throw new IllegalArgumentException("Player number must be 1 or 2");
        }
        profileBeingEdited = playerNumber;
        navigateTo(AppScreen.PROFILE_EDITOR);
    }

    public int getProfileBeingEdited() {
        return profileBeingEdited;
    }

    public void saveProfile(String name, int avatarIndex) {
        PlayerProfile profile = new PlayerProfile(name, avatarIndex);
        if (profileBeingEdited == 1) {
            playerOne.setValue(profile);
        } else {
            playerTwo.setValue(profile);
        }
    }

    public LiveData<PlayerProfile> getPlayerOne() {
        return playerOne;
    }

    public LiveData<PlayerProfile> getPlayerTwo() {
        return playerTwo;
    }

    public PlayerProfile getProfile(int playerNumber) {
        return playerNumber == 1 ? playerOne.getValue() : playerTwo.getValue();
    }

    public LiveData<PlayerStatistics> getPlayerOneStats() {
        return playerOneStats;
    }

    public LiveData<PlayerStatistics> getPlayerTwoStats() {
        return playerTwoStats;
    }

    public void setSetup(BoardSize size, GameMode mode, DiscPalette palette) {
        boardSize.setValue(size);
        gameMode.setValue(mode);
        discPalette.setValue(palette);
    }

    public BoardSize requireBoardSize() {
        return requireValue(boardSize, "board size");
    }

    public GameMode requireGameMode() {
        return requireValue(gameMode, "game mode");
    }

    public DiscPalette requireDiscPalette() {
        return requireValue(discPalette, "disc palette");
    }

    public void recordOutcome(GameStatus result) {
        PlayerStatistics first = requireValue(playerOneStats, "player one statistics");
        PlayerStatistics second = requireValue(playerTwoStats, "player two statistics");
        if (result == GameStatus.PLAYER_ONE_WON) {
            playerOneStats.setValue(first.withWin());
            playerTwoStats.setValue(second.withLoss());
        } else if (result == GameStatus.PLAYER_TWO_WON) {
            playerOneStats.setValue(first.withLoss());
            playerTwoStats.setValue(second.withWin());
        } else if (result == GameStatus.DRAW) {
            playerOneStats.setValue(first.withDraw());
            playerTwoStats.setValue(second.withDraw());
        }
    }

    public void clearStatistics() {
        playerOneStats.setValue(new PlayerStatistics());
        playerTwoStats.setValue(new PlayerStatistics());
    }

    private static <T> T requireValue(LiveData<T> liveData, String label) {
        T value = liveData.getValue();
        if (value == null) {
            throw new IllegalStateException("Missing " + label);
        }
        return value;
    }
}
