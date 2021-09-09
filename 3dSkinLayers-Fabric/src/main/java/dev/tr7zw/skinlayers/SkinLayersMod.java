package dev.tr7zw.skinlayers;

import net.fabricmc.api.ClientModInitializer;

public class SkinLayersMod extends SkinLayersModBase implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        this.onInitialize();
    }

}
