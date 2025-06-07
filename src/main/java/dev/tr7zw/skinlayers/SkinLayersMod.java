package dev.tr7zw.skinlayers;

//#if FABRIC
import net.fabricmc.api.ClientModInitializer;

public class SkinLayersMod extends SkinLayersModBase implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        this.onInitialize();
    }

    //#else
    //$$ import dev.tr7zw.transition.loader.ModLoaderUtil;
    //$$ public class SkinLayersMod extends SkinLayersModBase {
    //$$ public SkinLayersMod() {
    //$$ 	ModLoaderUtil.registerClientSetupListener(this::onInitialize);
    //$$ }
    //#endif

}
