package dev.tr7zw.skinlayers;

//spotless:off 
//#if FABRIC
import net.fabricmc.api.ClientModInitializer;

public class SkinLayersMod extends SkinLayersModBase implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        this.onInitialize();
    }

    
//#elseif FORGE
//$$	
	//$$	import net.minecraftforge.fml.ModLoadingContext;
	//$$	import net.minecraftforge.fml.common.Mod;
	//$$	import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
	//$$	import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
    //$$    import dev.tr7zw.skinlayers.config.ConfigScreenProvider;
    //$$    import dev.tr7zw.config.CustomConfigScreen;
    //$$    import dev.tr7zw.skinlayers.config.ConfigScreenProvider;
  //$$
    //#if MC <= 11605
    //$$ import net.minecraftforge.fml.ExtensionPoint;
    //$$ import net.minecraftforge.fml.network.FMLNetworkConstants;
    //$$ import org.apache.commons.lang3.tuple.Pair;
    //#elseif MC <= 11701
	//$$	import net.minecraftforge.fml.IExtensionPoint;
   //$$ import net.minecraftforge.fmlclient.ConfigGuiHandler.ConfigGuiFactory;
    //#elseif MC <= 11802
	//$$	import net.minecraftforge.fml.IExtensionPoint;
   //$$ import net.minecraftforge.client.ConfigGuiHandler.ConfigGuiFactory;
   //#else
    //$$ import net.minecraftforge.fml.IExtensionPoint;
  //$$ import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
   //#endif 
	//$$	@Mod("skinlayers3d")
  //$$ public class SkinLayersMod extends SkinLayersModBase {
  //$$
  //$$      //Forge only
  //$$      private boolean onServer = false;
  //$$      
  //$$     public SkinLayersMod() {
  //$$          try {
  //$$             Class clientClass = net.minecraft.client.Minecraft.class;
  //$$         }catch(Throwable ex) {
  //$$            System.out.println("3dSkinLayers Mod installed on a Server. Going to sleep.");
  //$$            onServer = true;
  //$$            return;
  //$$        }
        //#if MC <= 11605
    	//$$         ModLoadingContext.get().registerExtensionPoint(
        //$$ ExtensionPoint.CONFIGGUIFACTORY,
        //$$ () -> (mc, screen) -> ConfigScreenProvider.createConfigScreen(screen));
    	//#elseif MC <= 11802
 		//$$ ModLoadingContext.get().registerExtensionPoint(ConfigGuiFactory.class, () -> new ConfigGuiFactory((mc, screen) -> {
    	//$$            return ConfigScreenProvider.createConfigScreen(screen);
    	//$$        }));
    	//#else
    	//$$ ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class, () -> new ConfigScreenFactory((mc, screen) -> {
    	//$$            return ConfigScreenProvider.createConfigScreen(screen);
    	//$$        }));
		//#endif 
    //#if MC <= 11605
    //$$ ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
    //$$ () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
    //#else
  //$$        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
  //$$                () -> new IExtensionPoint.DisplayTest(
  //$$                       () -> ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString(),
  //$$                        (remote, isServer) -> true));
    //#endif
  //$$       FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
  //$$   }
  //$$
  //$$    private void setup(final FMLCommonSetupEvent event) {
  //$$        if(onServer)return;
  //$$        onInitialize();
  //$$    }
  //#elseif NEOFORGE
  //$$  import net.neoforged.fml.IExtensionPoint;
  //$$  import net.neoforged.fml.ModLoadingContext;
  //$$  import net.neoforged.fml.common.Mod;
  //$$  import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
  //$$  import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
  //$$  import net.neoforged.neoforge.client.ConfigScreenHandler.ConfigScreenFactory;
  //$$  import dev.tr7zw.skinlayers.config.ConfigScreenProvider;
  //$$
  //$$  @Mod("skinlayers3d")
  //$$  public class SkinLayersMod extends SkinLayersModBase {
  //$$
  //$$     // Forge only
  //$$      private boolean onServer = false;
  //$$
  //$$     public SkinLayersMod() {
  //$$         try {
  //$$             Class clientClass = net.minecraft.client.Minecraft.class;
  //$$         } catch (Throwable ex) {
  //$$             System.out.println("3dSkinLayers Mod installed on a Server. Going to sleep.");
  //$$             onServer = true;
  //$$             return;
  //$$        }
  //$$        ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class,
  //$$                () -> new ConfigScreenFactory((mc, screen) -> {
  //$$                    return ConfigScreenProvider.createConfigScreen(screen);
  //$$                }));
  //$$        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
  //$$                () -> new IExtensionPoint.DisplayTest(
  //$$                        () -> ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString(),
  //$$                       (remote, isServer) -> true));
  //$$        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
  //$$    }
  //$$
  //$$    private void setup(final FMLCommonSetupEvent event) {
  //$$        if (onServer)
  //$$            return;
  //$$       onInitialize();
  //$$    }
//#endif
 // spotless:on

}
