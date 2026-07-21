package io.github.himath2002.gridlinefour.model;

import java.util.Objects;

/** Immutable player identity used throughout a local session. */
public final class PlayerProfile {
    private final String name;
    private final int avatarIndex;

    public PlayerProfile(String name, int avatarIndex) {
        String normalizedName = Objects.requireNonNull(name, "name").trim();
        if (normalizedName.isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be empty");
        }
        if (avatarIndex < 0 || avatarIndex > 5) {
            throw new IllegalArgumentException("Avatar index must be between 0 and 5");
        }
        this.name = normalizedName;
        this.avatarIndex = avatarIndex;
    }

    public String getName() {
        return name;
    }

    public int getAvatarIndex() {
        return avatarIndex;
    }
}
