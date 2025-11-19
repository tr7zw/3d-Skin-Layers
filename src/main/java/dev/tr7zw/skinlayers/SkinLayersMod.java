package dev.tr7zw.skinlayers;

//? if fabric {

import net.fabricmc.api.ClientModInitializer;

public class SkinLayersMod extends SkinLayersModBase implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        this.onInitialize();
    }

    //? } else {
    /*
     import dev.tr7zw.transition.loader.ModLoaderUtil;
     public class SkinLayersMod extends SkinLayersModBase {
     public SkinLayersMod() {
     	ModLoaderUtil.registerClientSetupListener(this::onInitialize);
     }
    *///? }

}
