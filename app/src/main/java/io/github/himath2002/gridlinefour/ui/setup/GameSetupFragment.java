package io.github.himath2002.gridlinefour.ui.setup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import io.github.himath2002.gridlinefour.R;
import io.github.himath2002.gridlinefour.databinding.FragmentGameSetupBinding;
import io.github.himath2002.gridlinefour.model.BoardSize;
import io.github.himath2002.gridlinefour.model.DiscPalette;
import io.github.himath2002.gridlinefour.model.GameMode;
import io.github.himath2002.gridlinefour.navigation.AppScreen;
import io.github.himath2002.gridlinefour.viewmodel.GameViewModel;
import io.github.himath2002.gridlinefour.viewmodel.SessionViewModel;

/** Collects match options and creates a fresh rules-engine session. */
public final class GameSetupFragment extends Fragment {
    private FragmentGameSetupBinding binding;
    private SessionViewModel sessionViewModel;
    private GameViewModel gameViewModel;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentGameSetupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        restoreSelections();
        binding.startButton.setOnClickListener(ignored -> startMatch());
        binding.backButton.setOnClickListener(
                ignored -> sessionViewModel.navigateTo(AppScreen.HOME));
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    private void restoreSelections() {
        BoardSize boardSize = sessionViewModel.requireBoardSize();
        binding.boardSizeGroup.check(boardSize == BoardSize.COMPACT
                ? R.id.board_compact
                : boardSize == BoardSize.EXPANDED ? R.id.board_expanded : R.id.board_classic);
        binding.gameModeGroup.check(sessionViewModel.requireGameMode() == GameMode.LOCAL_PLAYERS
                ? R.id.mode_local
                : R.id.mode_computer);
        binding.paletteGroup.check(sessionViewModel.requireDiscPalette() == DiscPalette.JADE_AND_VIOLET
                ? R.id.palette_jade_violet
                : R.id.palette_coral_gold);
    }

    private void startMatch() {
        if (sessionViewModel.getProfile(1) == null) {
            Snackbar.make(binding.getRoot(), R.string.missing_player_one, Snackbar.LENGTH_LONG).show();
            return;
        }

        BoardSize boardSize = binding.boardCompact.isChecked()
                ? BoardSize.COMPACT
                : binding.boardExpanded.isChecked() ? BoardSize.EXPANDED : BoardSize.CLASSIC;
        GameMode mode = binding.modeLocal.isChecked() ? GameMode.LOCAL_PLAYERS : GameMode.COMPUTER;
        DiscPalette palette = binding.paletteJadeViolet.isChecked()
                ? DiscPalette.JADE_AND_VIOLET
                : DiscPalette.CORAL_AND_GOLD;

        if (mode == GameMode.LOCAL_PLAYERS && sessionViewModel.getProfile(2) == null) {
            Snackbar.make(binding.getRoot(), R.string.missing_player_two, Snackbar.LENGTH_LONG).show();
            return;
        }

        sessionViewModel.setSetup(boardSize, mode, palette);
        gameViewModel.startNewGame(boardSize, mode);
        sessionViewModel.navigateTo(AppScreen.GAME);
    }
}
