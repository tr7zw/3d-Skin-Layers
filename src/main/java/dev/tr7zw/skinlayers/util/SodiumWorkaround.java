package dev.tr7zw.skinlayers.util;

public class SodiumWorkaround {

    public static final boolean IS_SODIUM_WORKAROUND_NEEDED = isSodiumWorkaroundNeeded(); // This is a workaround to ensure the ModelPartData is loaded
    public static final boolean IS_SODIUM_LOADED = isSodiumLoaded();

    private static boolean isSodiumWorkaroundNeeded() {
        try {
            Class.forName("me.jellysquid.mods.sodium.client.render.immediate.model.ModelPartData");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean isSodiumLoaded() {
        try {
            Class.forName("net.caffeinemc.mods.sodium.client.render.immediate.model.ModelCuboid");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
