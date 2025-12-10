package dev.tr7zw.skinlayers.api;

import java.util.Collections;

import com.mojang.blaze3d.platform.NativeImage;

import dev.tr7zw.skinlayers.render.CustomizableCubeListBuilder;
import dev.tr7zw.skinlayers.render.CustomizableModelPart;
import dev.tr7zw.skinlayers.util.NMSWrapper.WrappedNativeImage;
import dev.tr7zw.skinlayers.versionless.util.wrapper.SolidPixelWrapper;
import lombok.*;
import lombok.experimental.*;
import net.minecraft.client.player.AbstractClientPlayer;

@UtilityClass
public class SkinLayersAPI {

    @Getter
    private static final MeshHelper meshHelper = new MeshHelperImplementation();
    @Getter
    private static final MeshProvider meshProvider = new MeshProviderImplementation();
    private static MeshTransformerProvider meshTransformerProvider = MeshTransformerProvider.EMPTY_PROVIDER;
    @Getter
    private static BoxBuilder boxBuilder = BoxBuilder.DEFAULT;

    public static void setupBoxBuilder(BoxBuilder builder) {
        SkinLayersAPI.boxBuilder = builder;
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
                boolean topPivot, float rotationOffset, boolean mirror) {
            CustomizableCubeListBuilder builder = new CustomizableCubeListBuilder();
            builder.mirror(mirror);
            if (SolidPixelWrapper.wrapBox(builder, new WrappedNativeImage(natImage), width, height, depth, textureU,
                    textureV, topPivot, rotationOffset) != null) {
                return new CustomizableModelPart(builder.getVanillaCubes(), builder.getCubes(), Collections.emptyMap());
            }
            return Mesh.EMPTY;
        }

        @Override
        public Mesh create3DMesh(NativeImage natImage, int width, int height, int depth, int textureU, int textureV,
                boolean topPivot, float rotationOffset) {
            return create3DMesh(natImage, width, height, depth, textureU, textureV, topPivot, rotationOffset, false);
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
