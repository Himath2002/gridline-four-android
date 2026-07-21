package io.github.himath2002.gridlinefour.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import io.github.himath2002.gridlinefour.R;
import io.github.himath2002.gridlinefour.databinding.FragmentProfileEditorBinding;
import io.github.himath2002.gridlinefour.model.PlayerProfile;
import io.github.himath2002.gridlinefour.navigation.AppScreen;
import io.github.himath2002.gridlinefour.viewmodel.SessionViewModel;

/** Creates or edits one of the two in-memory local profiles. */
public final class ProfileEditorFragment extends Fragment {
    private FragmentProfileEditorBinding binding;
    private SessionViewModel sessionViewModel;
    private int selectedAvatar;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentProfileEditorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
        int playerNumber = sessionViewModel.getProfileBeingEdited();
        binding.profileHeading.setText(getString(
                R.string.profile_title,
                getString(playerNumber == 1 ? R.string.player_one : R.string.player_two)));

        PlayerProfile currentProfile = sessionViewModel.getProfile(playerNumber);
        if (currentProfile != null) {
            binding.playerNameInput.setText(currentProfile.getName());
            selectedAvatar = currentProfile.getAvatarIndex();
        }

        ImageButton[] choices = {
                binding.avatarOne,
                binding.avatarTwo,
                binding.avatarThree,
                binding.avatarFour,
                binding.avatarFive,
                binding.avatarSix
        };
        for (int index = 0; index < choices.length; index++) {
            int avatarIndex = index;
            choices[index].setContentDescription(getString(R.string.avatar_choice, index + 1));
            choices[index].setOnClickListener(ignored -> {
                selectedAvatar = avatarIndex;
                renderSelection(choices);
            });
        }
        renderSelection(choices);

        binding.saveButton.setOnClickListener(ignored -> saveProfile());
        binding.cancelButton.setOnClickListener(
                ignored -> sessionViewModel.navigateTo(AppScreen.HOME));
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    private void renderSelection(ImageButton[] choices) {
        for (int index = 0; index < choices.length; index++) {
            boolean selected = index == selectedAvatar;
            choices[index].setAlpha(selected ? 1.0f : 0.55f);
            choices[index].setScaleX(selected ? 1.0f : 0.92f);
            choices[index].setScaleY(selected ? 1.0f : 0.92f);
            choices[index].setSelected(selected);
        }
    }

    private void saveProfile() {
        if (binding == null) {
            return;
        }
        String name = binding.playerNameInput.getText() == null
                ? ""
                : binding.playerNameInput.getText().toString().trim();
        if (name.isEmpty()) {
            binding.playerNameInput.setError(getString(R.string.name_required));
            return;
        }
        sessionViewModel.saveProfile(name, selectedAvatar);
        sessionViewModel.navigateTo(AppScreen.HOME);
    }
}
