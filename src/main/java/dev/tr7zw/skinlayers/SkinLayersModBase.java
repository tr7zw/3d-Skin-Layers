package dev.tr7zw.skinlayers;

import java.util.Set;

import com.google.common.collect.Sets;

import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.config.ConfigScreenProvider;
import dev.tr7zw.skinlayers.versionless.ModBase;
import dev.tr7zw.transition.loader.ModLoaderUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public abstract class SkinLayersModBase extends ModBase {

    public static SkinLayersModBase instance;
    // TODO: Move somewhere else
    public static final Set<Item> hideHeadLayers = Sets.newHashSet(Items.ZOMBIE_HEAD, Items.CREEPER_HEAD,
            Items.DRAGON_HEAD, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL);

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
