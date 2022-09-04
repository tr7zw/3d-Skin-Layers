package dev.tr7zw.skinlayers.api;

import com.mojang.blaze3d.platform.NativeImage;

import dev.tr7zw.skinlayers.render.SolidPixelWrapper;
import net.minecraft.client.player.AbstractClientPlayer;

public class SkinLayersAPI {

    private static final MeshHelper meshHelper = new MeshHelperImplementation();
    private static final MeshProvider meshProvider = new MeshProviderImplementation();
    private static MeshTransformerProvider meshTransformerProvider = MeshTransformerProvider.EMPTY_PROVIDER;
    
    
    private SkinLayersAPI() {
        // private
    }
    
    public static MeshHelper getMeshHelper() {
        return meshHelper;
    }
    
    public static MeshProvider getMeshProvider() {
        return meshProvider;
    }
    
    public static void setupMeshTransformerProvider(MeshTransformerProvider provider) {
        SkinLayersAPI.meshTransformerProvider = provider;
    }
    
    public static MeshTransformerProvider getMeshTransformerProvider() {
        return meshTransformerProvider;
    }
    
    private static class MeshHelperImplementation implements MeshHelper {

        @Override
        public Mesh create3DMesh(NativeImage natImage, int width, int height, int depth, int textureU, int textureV,
                boolean topPivot, float rotationOffset) {
            return SolidPixelWrapper.wrapBox(natImage, width, height, depth, textureU, textureV, topPivot, rotationOffset);
        }
        
    }
    
    private static class MeshProviderImplementation implements MeshProvider {

        @Override
        public PlayerData getPlayerMesh(AbstractClientPlayer abstractClientPlayerEntity) {
            if(abstractClientPlayerEntity instanceof PlayerData data) {
                return data;
            }
            return null;
        }
        
    }
  
    
}
