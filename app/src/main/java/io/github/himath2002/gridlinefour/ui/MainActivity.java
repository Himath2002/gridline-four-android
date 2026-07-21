package io.github.himath2002.gridlinefour.ui;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import io.github.himath2002.gridlinefour.R;
import io.github.himath2002.gridlinefour.databinding.ActivityMainBinding;
import io.github.himath2002.gridlinefour.navigation.AppScreen;
import io.github.himath2002.gridlinefour.ui.game.GameFragment;
import io.github.himath2002.gridlinefour.ui.home.HomeFragment;
import io.github.himath2002.gridlinefour.ui.profile.ProfileEditorFragment;
import io.github.himath2002.gridlinefour.ui.session.SessionFragment;
import io.github.himath2002.gridlinefour.ui.setup.GameSetupFragment;
import io.github.himath2002.gridlinefour.viewmodel.GameViewModel;
import io.github.himath2002.gridlinefour.viewmodel.SessionViewModel;

/** Hosts the app's focused fragment flow and translates navigation state into screens. */
public final class MainActivity extends AppCompatActivity {
    private SessionViewModel sessionViewModel;
    private GameViewModel gameViewModel;
    private AppScreen renderedScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionViewModel = new ViewModelProvider(this).get(SessionViewModel.class);
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        sessionViewModel.getScreen().observe(this, this::renderScreen);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackNavigation();
            }
        });
    }

    private void renderScreen(AppScreen screen) {
        if (screen == null || screen == renderedScreen) {
            return;
        }
        renderedScreen = screen;
        Fragment fragment;
        switch (screen) {
            case PROFILE_EDITOR:
                fragment = new ProfileEditorFragment();
                break;
            case GAME_SETUP:
                fragment = new GameSetupFragment();
                break;
            case GAME:
                fragment = new GameFragment();
                break;
            case SETTINGS:
                fragment = new SessionFragment();
                break;
            case HOME:
            default:
                fragment = new HomeFragment();
                break;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.screen_container, fragment)
                .commit();
    }

    private void handleBackNavigation() {
        AppScreen screen = sessionViewModel.getScreen().getValue();
        if (screen == AppScreen.GAME) {
            sessionViewModel.navigateTo(AppScreen.SETTINGS);
        } else if (screen == AppScreen.SETTINGS && gameViewModel.hasGame()) {
            sessionViewModel.navigateTo(AppScreen.GAME);
        } else if (screen != AppScreen.HOME) {
            sessionViewModel.navigateTo(AppScreen.HOME);
        } else {
            finish();
        }
    }
}
