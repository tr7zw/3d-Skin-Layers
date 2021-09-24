package dev.tr7zw.skinlayers;

public class EntityCullingMod extends SkinLayersModBase {

    public EntityCullingMod() {
        try {
            Class clientClass = net.minecraft.client.Minecraft.class;
        }catch(Throwable ex) {
            System.out.println("3dSkinLayers Mod installed on a Server. Going to sleep.");
            return;
        }
        onInitialize();
    }
    
}
