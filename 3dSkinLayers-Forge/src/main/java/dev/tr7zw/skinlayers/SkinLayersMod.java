package dev.tr7zw.skinlayers;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

@Mod(modid = "skinlayers3d", name = "3dSkinLayers", version = "1.1.0", clientSideOnly = true, guiFactory = "dev.tr7zw.skinlayers.config.GuiFactory")
public class SkinLayersMod extends SkinLayersModBase {

    //Forge only
    private boolean onServer = false;
    
    public SkinLayersMod() {
        try {
            Class clientClass = net.minecraft.client.Minecraft.class;
        }catch(Throwable ex) {
            System.out.println("EntityCulling Mod installed on a Server. Going to sleep.");
            onServer = true;
            return;
        }
        onInitialize();
    }
    
    @EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    
}
