package io.github.himath2002.gridlinefour.ui.common;

import androidx.annotation.DrawableRes;

import io.github.himath2002.gridlinefour.R;

/** Maps the six stable avatar identifiers stored in a profile to vector artwork. */
public final class AvatarCatalog {
    private static final int[] AVATARS = {
            R.drawable.avatar_orbit,
            R.drawable.avatar_comet,
            R.drawable.avatar_spark,
            R.drawable.avatar_leaf,
            R.drawable.avatar_prism,
            R.drawable.avatar_wave
    };

    private AvatarCatalog() {
    }

    @DrawableRes
    public static int resourceFor(int avatarIndex) {
        if (avatarIndex < 0 || avatarIndex >= AVATARS.length) {
            return AVATARS[0];
        }
        return AVATARS[avatarIndex];
    }
}
