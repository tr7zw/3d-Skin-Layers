package dev.tr7zw.skinlayers.util;

public class SodiumWorkaround {

    public static final boolean IS_SODIUM_WORKAROUND_NEEDED = isSodiumWorkaroundNeeded(); // This is a workaround to ensure the ModelPartData is loaded

    private static boolean isSodiumWorkaroundNeeded() {
        try {
            Class.forName("me.jellysquid.mods.sodium.client.render.immediate.model.ModelPartData");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
