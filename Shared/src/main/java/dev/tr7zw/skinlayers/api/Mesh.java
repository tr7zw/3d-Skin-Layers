package dev.tr7zw.skinlayers.api;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;

public interface Mesh {

    public default void render(PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay) {
        render(poseStack, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha);
    
    public void setPosition(float x, float y, float z);

    public void setRotation(float xRot, float yRot, float zRot);
    
    public void loadPose(PartPose partPose);

    public void copyFrom(ModelPart modelPart);
    
    public void setVisible(boolean visible);
    
    public boolean isVisible();
    
}
