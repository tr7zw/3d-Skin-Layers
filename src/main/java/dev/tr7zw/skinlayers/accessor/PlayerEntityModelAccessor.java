package dev.tr7zw.skinlayers.accessor;

/**
 * Used to expose the thinArms setting of the player model
 *
 */
public interface PlayerEntityModelAccessor {
    public boolean hasThinArms();

    void setIgnored(boolean ignore);
}
