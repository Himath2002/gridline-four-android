package io.github.himath2002.gridlinefour.ui.game;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import io.github.himath2002.gridlinefour.R;
import io.github.himath2002.gridlinefour.databinding.FragmentGameBinding;
import io.github.himath2002.gridlinefour.game.Disc;
import io.github.himath2002.gridlinefour.game.GameSnapshot;
import io.github.himath2002.gridlinefour.game.GameStatus;
import io.github.himath2002.gridlinefour.game.MoveResult;
import io.github.himath2002.gridlinefour.model.DiscPalette;
import io.github.himath2002.gridlinefour.model.GameMode;
import io.github.himath2002.gridlinefour.model.PlayerProfile;
import io.github.himath2002.gridlinefour.navigation.AppScreen;
import io.github.himath2002.gridlinefour.ui.common.AvatarCatalog;
import io.github.himath2002.gridlinefour.viewmodel.GameViewModel;
import io.github.himath2002.gridlinefour.viewmodel.SessionViewModel;

/** Renders a lifecycle-aware board while delegating every rule to GameViewModel. */
public final class GameFragment extends Fragment {
    private FragmentGameBinding binding;
    private SessionViewModel sessionViewModel;
    private GameViewModel gameViewModel;
    private MaterialButton[][] cells;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentGameBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        if (!gameViewModel.hasGame()) {
            sessionViewModel.navigateTo(AppScreen.GAME_SETUP);
            return;
        }

        renderProfiles();
        binding.undoButton.setVisibility(sessionViewModel.requireGameMode() == GameMode.COMPUTER
                ? View.GONE
                : View.VISIBLE);
        binding.restartButton.setOnClickListener(ignored -> gameViewModel.resetGame());
        binding.undoButton.setOnClickListener(ignored -> {
            if (!gameViewModel.undoLastMove()) {
                Snackbar.make(binding.getRoot(), R.string.nothing_to_undo, Snackbar.LENGTH_SHORT).show();
            }
        });
        binding.sessionButton.setOnClickListener(
                ignored -> sessionViewModel.navigateTo(AppScreen.SETTINGS));
        gameViewModel.getSnapshot().observe(getViewLifecycleOwner(), this::renderSnapshot);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        cells = null;
        super.onDestroyView();
    }

    private void renderProfiles() {
        PlayerProfile first = sessionViewModel.getProfile(1);
        if (first != null) {
            binding.playerOneName.setText(first.getName());
            binding.playerOneAvatar.setImageResource(AvatarCatalog.resourceFor(first.getAvatarIndex()));
        }

        if (sessionViewModel.requireGameMode() == GameMode.COMPUTER) {
            binding.playerTwoName.setText(R.string.computer);
            binding.playerTwoAvatar.setImageResource(R.drawable.ic_gridline_brand);
            return;
        }
        PlayerProfile second = sessionViewModel.getProfile(2);
        if (second != null) {
            binding.playerTwoName.setText(second.getName());
            binding.playerTwoAvatar.setImageResource(AvatarCatalog.resourceFor(second.getAvatarIndex()));
        }
    }

    private void renderSnapshot(GameSnapshot snapshot) {
        if (snapshot == null || binding == null) {
            return;
        }
        if (cells == null
                || cells.length != snapshot.getRows()
                || cells[0].length != snapshot.getColumns()) {
            buildBoard(snapshot.getRows(), snapshot.getColumns());
        }

        Disc[][] board = snapshot.getBoard();
        for (int row = 0; row < snapshot.getRows(); row++) {
            for (int column = 0; column < snapshot.getColumns(); column++) {
                Disc disc = board[row][column];
                cells[row][column].setBackgroundTintList(ColorStateList.valueOf(colorFor(disc)));
                cells[row][column].setContentDescription(getString(
                        R.string.disc_position,
                        row + 1,
                        column + 1,
                        getString(labelFor(disc))));
                cells[row][column].setEnabled(snapshot.getStatus() == GameStatus.IN_PROGRESS);
            }
        }

        binding.movesMadeText.setText(getString(R.string.moves_made, snapshot.getMoveCount()));
        binding.movesRemainingText.setText(
                getString(R.string.moves_remaining, snapshot.getRemainingMoves()));
        renderStatus(snapshot);

        GameStatus completedOutcome = gameViewModel.consumeCompletedOutcome();
        if (completedOutcome != null) {
            sessionViewModel.recordOutcome(completedOutcome);
        }
    }

    private void buildBoard(int rows, int columns) {
        binding.boardGrid.removeAllViews();
        binding.boardGrid.setRowCount(rows);
        binding.boardGrid.setColumnCount(columns);
        cells = new MaterialButton[rows][columns];

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                MaterialButton cell = new MaterialButton(requireContext());
                cell.setText("");
                cell.setMinWidth(0);
                cell.setMinHeight(0);
                cell.setInsetTop(0);
                cell.setInsetBottom(0);
                cell.setCornerRadius(dp(40));
                cell.setPadding(0, 0, 0, 0);
                int selectedColumn = column;
                cell.setOnClickListener(ignored -> playColumn(selectedColumn));

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(row, 1f);
                params.columnSpec = GridLayout.spec(column, 1f);
                params.width = 0;
                params.height = dp(44);
                int margin = dp(3);
                params.setMargins(margin, margin, margin, margin);
                cell.setLayoutParams(params);
                binding.boardGrid.addView(cell);
                cells[row][column] = cell;
            }
        }
    }

    private void playColumn(int column) {
        MoveResult result = gameViewModel.playColumn(column);
        if (result.getRejection() == MoveResult.Rejection.COLUMN_FULL) {
            Snackbar.make(binding.getRoot(), R.string.column_full, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void renderStatus(GameSnapshot snapshot) {
        if (snapshot.getStatus() == GameStatus.DRAW) {
            binding.statusText.setText(R.string.draw_message);
        } else if (snapshot.getStatus() == GameStatus.PLAYER_ONE_WON) {
            binding.statusText.setText(getString(R.string.winner_message, playerName(Disc.PLAYER_ONE)));
        } else if (snapshot.getStatus() == GameStatus.PLAYER_TWO_WON) {
            binding.statusText.setText(getString(R.string.winner_message, playerName(Disc.PLAYER_TWO)));
        } else {
            binding.statusText.setText(getString(
                    R.string.turn_message,
                    playerName(snapshot.getCurrentTurn())));
        }
    }

    private String playerName(Disc disc) {
        if (disc == Disc.PLAYER_ONE) {
            PlayerProfile first = sessionViewModel.getProfile(1);
            return first == null ? getString(R.string.player_one) : first.getName();
        }
        if (sessionViewModel.requireGameMode() == GameMode.COMPUTER) {
            return getString(R.string.computer);
        }
        PlayerProfile second = sessionViewModel.getProfile(2);
        return second == null ? getString(R.string.player_two) : second.getName();
    }

    @ColorInt
    private int colorFor(Disc disc) {
        int resource;
        if (disc == Disc.EMPTY) {
            resource = R.color.empty_disc;
        } else if (sessionViewModel.requireDiscPalette() == DiscPalette.JADE_AND_VIOLET) {
            resource = disc == Disc.PLAYER_ONE ? R.color.jade : R.color.violet;
        } else {
            resource = disc == Disc.PLAYER_ONE ? R.color.coral : R.color.gold;
        }
        return ContextCompat.getColor(requireContext(), resource);
    }

    private int labelFor(Disc disc) {
        if (disc == Disc.PLAYER_ONE) {
            return R.string.player_one_disc;
        }
        if (disc == Disc.PLAYER_TWO) {
            return R.string.player_two_disc;
        }
        return R.string.empty_disc;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
