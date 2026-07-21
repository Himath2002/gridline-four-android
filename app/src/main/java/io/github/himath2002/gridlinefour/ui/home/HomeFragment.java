package io.github.himath2002.gridlinefour.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import io.github.himath2002.gridlinefour.R;
import io.github.himath2002.gridlinefour.databinding.FragmentHomeBinding;
import io.github.himath2002.gridlinefour.model.PlayerProfile;
import io.github.himath2002.gridlinefour.navigation.AppScreen;
import io.github.himath2002.gridlinefour.ui.common.AvatarCatalog;
import io.github.himath2002.gridlinefour.viewmodel.SessionViewModel;

/** Entry screen for local profile setup and match creation. */
public final class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private SessionViewModel sessionViewModel;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
        sessionViewModel.getPlayerOne().observe(getViewLifecycleOwner(), this::renderPlayerOne);
        sessionViewModel.getPlayerTwo().observe(getViewLifecycleOwner(), this::renderPlayerTwo);

        binding.playerOneAction.setOnClickListener(ignored -> sessionViewModel.editProfile(1));
        binding.playerTwoAction.setOnClickListener(ignored -> sessionViewModel.editProfile(2));
        binding.setupButton.setOnClickListener(
                ignored -> sessionViewModel.navigateTo(AppScreen.GAME_SETUP));
        binding.closeButton.setOnClickListener(ignored -> showCloseConfirmation());
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    private void renderPlayerOne(PlayerProfile profile) {
        renderProfile(profile, true);
    }

    private void renderPlayerTwo(PlayerProfile profile) {
        renderProfile(profile, false);
    }

    private void renderProfile(PlayerProfile profile, boolean firstPlayer) {
        if (binding == null) {
            return;
        }
        if (firstPlayer) {
            if (profile != null) {
                binding.playerOneName.setText(profile.getName());
                binding.playerOneAvatar.setImageResource(AvatarCatalog.resourceFor(profile.getAvatarIndex()));
                binding.playerOneAction.setText(R.string.edit_profile);
            } else {
                binding.playerOneName.setText(R.string.profile_not_created);
                binding.playerOneAction.setText(R.string.create_profile);
            }
        } else if (profile != null) {
            binding.playerTwoName.setText(profile.getName());
            binding.playerTwoAvatar.setImageResource(AvatarCatalog.resourceFor(profile.getAvatarIndex()));
            binding.playerTwoAction.setText(R.string.edit_profile);
        } else {
            binding.playerTwoName.setText(R.string.profile_not_created);
            binding.playerTwoAction.setText(R.string.create_profile);
        }
    }

    private void showCloseConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.close_app_title)
                .setMessage(R.string.close_app_message)
                .setPositiveButton(R.string.close, (dialog, which) -> requireActivity().finish())
                .setNegativeButton(R.string.stay, null)
                .show();
    }
}
