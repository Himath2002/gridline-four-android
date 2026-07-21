package io.github.himath2002.gridlinefour.ui.session;

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
import io.github.himath2002.gridlinefour.databinding.FragmentSessionBinding;
import io.github.himath2002.gridlinefour.model.GameMode;
import io.github.himath2002.gridlinefour.model.PlayerProfile;
import io.github.himath2002.gridlinefour.model.PlayerStatistics;
import io.github.himath2002.gridlinefour.navigation.AppScreen;
import io.github.himath2002.gridlinefour.viewmodel.GameViewModel;
import io.github.himath2002.gridlinefour.viewmodel.SessionViewModel;

/** Displays session-only records and the deliberate navigation choices around a match. */
public final class SessionFragment extends Fragment {
    private FragmentSessionBinding binding;
    private SessionViewModel sessionViewModel;
    private GameViewModel gameViewModel;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentSessionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        sessionViewModel.getPlayerOneStats().observe(getViewLifecycleOwner(), ignored -> renderRecords());
        sessionViewModel.getPlayerTwoStats().observe(getViewLifecycleOwner(), ignored -> renderRecords());
        sessionViewModel.getPlayerOne().observe(getViewLifecycleOwner(), ignored -> renderRecords());
        sessionViewModel.getPlayerTwo().observe(getViewLifecycleOwner(), ignored -> renderRecords());

        binding.resumeButton.setEnabled(gameViewModel.hasGame());
        binding.resumeButton.setOnClickListener(
                ignored -> sessionViewModel.navigateTo(AppScreen.GAME));
        binding.setupButton.setOnClickListener(
                ignored -> sessionViewModel.navigateTo(AppScreen.GAME_SETUP));
        binding.homeButton.setOnClickListener(
                ignored -> sessionViewModel.navigateTo(AppScreen.HOME));
        binding.clearButton.setOnClickListener(ignored -> {
            sessionViewModel.clearStatistics();
            Snackbar.make(binding.getRoot(), R.string.statistics_cleared, Snackbar.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    private void renderRecords() {
        if (binding == null) {
            return;
        }
        PlayerProfile first = sessionViewModel.getProfile(1);
        PlayerProfile second = sessionViewModel.getProfile(2);
        PlayerStatistics firstStats = sessionViewModel.getPlayerOneStats().getValue();
        PlayerStatistics secondStats = sessionViewModel.getPlayerTwoStats().getValue();
        if (firstStats == null || secondStats == null) {
            return;
        }

        binding.playerOneHeading.setText(first == null
                ? getString(R.string.player_one)
                : first.getName());
        binding.playerTwoHeading.setText(sessionViewModel.requireGameMode() == GameMode.COMPUTER
                ? getString(R.string.computer)
                : second == null ? getString(R.string.player_two) : second.getName());
        renderStatistics(firstStats, binding.playerOneRecord, binding.playerOneRate);
        renderStatistics(secondStats, binding.playerTwoRecord, binding.playerTwoRate);
    }

    private void renderStatistics(
            PlayerStatistics statistics,
            android.widget.TextView recordView,
            android.widget.TextView rateView
    ) {
        String wins = getResources().getQuantityString(
                R.plurals.wins_count,
                statistics.getWins(),
                statistics.getWins());
        String losses = getResources().getQuantityString(
                R.plurals.losses_count,
                statistics.getLosses(),
                statistics.getLosses());
        String draws = getResources().getQuantityString(
                R.plurals.draws_count,
                statistics.getDraws(),
                statistics.getDraws());
        recordView.setText(getString(R.string.record_summary, wins, losses, draws));
        if (statistics.getGamesPlayed() == 0) {
            rateView.setText(R.string.no_games);
        } else {
            String games = getResources().getQuantityString(
                    R.plurals.games_count,
                    statistics.getGamesPlayed(),
                    statistics.getGamesPlayed());
            rateView.setText(getString(
                    R.string.win_rate_summary,
                    statistics.getWinRate(),
                    games));
        }
    }
}
