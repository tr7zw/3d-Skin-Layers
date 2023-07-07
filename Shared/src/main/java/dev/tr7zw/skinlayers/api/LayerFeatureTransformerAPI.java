package dev.tr7zw.skinlayers.api;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;

public class LayerFeatureTransformerAPI {

    private static LayerTransformer layerTransformer = LayerTransformer.IDENTITY;

    public static LayerTransformer getTransformer() {
        return layerTransformer;
    }

    public static void setLayerTransformer(LayerTransformer layerTransformer) {
        LayerFeatureTransformerAPI.layerTransformer = layerTransformer;
    }

    @FunctionalInterface
    public interface LayerTransformer {
        LayerTransformer IDENTITY = (abstractClientPlayer, matrixStack, modelPart) -> {
        };

        void transform(AbstractClientPlayer abstractClientPlayer, PoseStack matrixStack, ModelPart modelPart);
    }
}
