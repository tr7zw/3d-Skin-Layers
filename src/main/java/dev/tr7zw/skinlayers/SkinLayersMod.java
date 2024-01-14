package dev.tr7zw.skinlayers;

//spotless:off 
//#if FABRIC
import net.fabricmc.api.ClientModInitializer;

public class SkinLayersMod extends SkinLayersModBase implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        this.onInitialize();
    }

    
  //#else
  //$$ import dev.tr7zw.util.ModLoaderUtil;
  //$$ public class SkinLayersMod extends SkinLayersModBase {
  //$$ public SkinLayersMod() {
  //$$ 	ModLoaderUtil.registerClientSetupListener(this::onInitialize);
  //$$ }
  //#endif
  //spotless:on

}
