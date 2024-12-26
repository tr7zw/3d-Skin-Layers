package dev.tr7zw.skinlayers.api;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
//#if MC >= 11700
import net.minecraft.client.model.geom.PartPose;
//#endif

public interface Mesh {

    public static final Mesh EMPTY = new Mesh() {

        @Override
        public void setVisible(boolean visible) {
        }

        @Override
        public void setRotation(float xRot, float yRot, float zRot) {
        }

        @Override
        public void setPosition(float x, float y, float z) {
        }

        @Override
        public void render(ModelPart vanillaModel, PoseStack poseStack, VertexConsumer vertexConsumer, int light,
                int overlay, int color) {
        }

        @Override
        //#if MC >= 11700
        public void loadPose(PartPose partPose) {
        //#else
          //$$ public void loadPose(ModelPart partPose) {
        //#endif
        }

        @Override
        public boolean isVisible() {
            return false;
        }

        @Override
        public void copyFrom(ModelPart modelPart) {
        }

        @Override
        public void reset() {
        }
    };

    public default void render(PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay) {
        render(null, poseStack, vertexConsumer, light, overlay, 0xFFFFFFFF);
    }

    /**
     * @param color The color, in ARGB format
     */
    public void render(ModelPart vanillaModel, PoseStack poseStack, VertexConsumer vertexConsumer, int light,
            int overlay, int color);

    public default void render(ModelPart vanillaModel, PoseStack poseStack, VertexConsumer vertexConsumer, int light,
            int overlay, float red, float green, float blue, float alpha) {
        int color = 0;
        int a = (int) (alpha * 255) << 24;
        int r = (int) (red * 255) << 16;
        int g = (int) (green * 255) << 8;
        int b = (int) (blue * 255);

        // Combine them into a single int
        color = a | r | g | b;
        render(vanillaModel, poseStack, vertexConsumer, light, overlay, color);
    }

    public void setPosition(float x, float y, float z);

    public void setRotation(float xRot, float yRot, float zRot);

    //#if MC >= 11700
    public void loadPose(PartPose partPose);
    //#else
      //$$ public void loadPose(ModelPart partPose);
    //#endif

    public void copyFrom(ModelPart modelPart);

    public void reset();

    public void setVisible(boolean visible);

    public boolean isVisible();

}
