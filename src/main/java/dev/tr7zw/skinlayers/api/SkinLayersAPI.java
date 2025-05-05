package dev.tr7zw.skinlayers.api;

import java.util.Collections;

import com.mojang.blaze3d.platform.NativeImage;

import dev.tr7zw.skinlayers.render.CustomizableCubeListBuilder;
import dev.tr7zw.skinlayers.render.CustomizableModelPart;
import dev.tr7zw.skinlayers.util.NMSWrapper.WrappedNativeImage;
import dev.tr7zw.skinlayers.versionless.util.wrapper.SolidPixelWrapper;
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
            CustomizableCubeListBuilder builder = new CustomizableCubeListBuilder();
            if (SolidPixelWrapper.wrapBox(builder, new WrappedNativeImage(natImage), width, height, depth, textureU,
                    textureV, topPivot, rotationOffset) != null) {
                return new CustomizableModelPart(builder.getVanillaCubes(), builder.getCubes(), Collections.emptyMap());
            }
            return Mesh.EMPTY;
        }

    }

    private static class MeshProviderImplementation implements MeshProvider {

        @Override
        public PlayerData getPlayerMesh(AbstractClientPlayer abstractClientPlayerEntity) {
            if (abstractClientPlayerEntity instanceof PlayerData) {
                return (PlayerData) abstractClientPlayerEntity;
            }
            return null;
        }

    }

}
