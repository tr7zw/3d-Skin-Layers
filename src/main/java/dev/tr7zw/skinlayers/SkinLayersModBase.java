package dev.tr7zw.skinlayers;

import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.config.ConfigScreenProvider;
import dev.tr7zw.skinlayers.versionless.ModBase;
import dev.tr7zw.util.ModLoaderUtil;
import net.minecraft.world.entity.player.Player;

public abstract class SkinLayersModBase extends ModBase {

    public static SkinLayersModBase instance;

    protected SkinLayersModBase() {
        instance = this;
        ModLoaderUtil.disableDisplayTest();
        ModLoaderUtil.registerConfigScreen(ConfigScreenProvider::createConfigScreen);
    }

    public void refreshLayers(Player player) {
        if (player == null || !(player instanceof PlayerSettings))
            return;
        PlayerSettings settings = (PlayerSettings) player;
        settings.clearMeshes();
        settings.setCurrentSkin(null);
    }

}
