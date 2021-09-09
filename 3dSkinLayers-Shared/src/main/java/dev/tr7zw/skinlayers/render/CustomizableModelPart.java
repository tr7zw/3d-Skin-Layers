package dev.tr7zw.skinlayers.render;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.model.geom.PartPose;

/**
 * Cut down copy of the Vanilla ModelPart to bypass Optifine/Sodium screwing
 * with the CustomizableCube class
 *
 */
public class CustomizableModelPart {

    public float x;
    public float y;
    public float z;
    public float xRot;
    public float yRot;
    public float zRot;
    public boolean visible = true;
    private final List<Cube> cubes;
    private final Map<String, ModelPart> children;

    public CustomizableModelPart(List<Cube> list, Map<String, ModelPart> map) {
        this.cubes = list;
        this.children = map;
    }

    public void loadPose(PartPose partPose) {
        this.x = partPose.x;
        this.y = partPose.y;
        this.z = partPose.z;
        this.xRot = partPose.xRot;
        this.yRot = partPose.yRot;
        this.zRot = partPose.zRot;
    }

    public void copyFrom(ModelPart modelPart) {
        this.xRot = modelPart.xRot;
        this.yRot = modelPart.yRot;
        this.zRot = modelPart.zRot;
        this.x = modelPart.x;
        this.y = modelPart.y;
        this.z = modelPart.z;
    }

    public ModelPart getChild(String string) {
        ModelPart modelPart = this.children.get(string);
        if (modelPart == null)
            throw new NoSuchElementException("Can't find part " + string);
        return modelPart;
    }

    public void setPos(float f, float g, float h) {
        this.x = f;
        this.y = g;
        this.z = h;
    }

    public void setRotation(float f, float g, float h) {
        this.xRot = f;
        this.yRot = g;
        this.zRot = h;
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j) {
        render(poseStack, vertexConsumer, i, j, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h,
            float k) {
        if (!this.visible)
            return;
        if (this.cubes.isEmpty() && this.children.isEmpty())
            return;
        poseStack.pushPose();
        translateAndRotate(poseStack);
        compile(poseStack.last(), vertexConsumer, i, j, f, g, h, k);
        for (ModelPart modelPart : this.children.values())
            modelPart.render(poseStack, vertexConsumer, i, j, f, g, h, k);
        poseStack.popPose();
    }

    public void translateAndRotate(PoseStack poseStack) {
        poseStack.translate((this.x / 16.0F), (this.y / 16.0F), (this.z / 16.0F));
        if (this.zRot != 0.0F)
            poseStack.mulPose(Vector3f.ZP.rotation(this.zRot));
        if (this.yRot != 0.0F)
            poseStack.mulPose(Vector3f.YP.rotation(this.yRot));
        if (this.xRot != 0.0F)
            poseStack.mulPose(Vector3f.XP.rotation(this.xRot));
    }

    private void compile(PoseStack.Pose pose, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h,
            float k) {
        for (Cube cube : this.cubes)
            cube.compile(pose, vertexConsumer, i, j, f, g, h, k);
    }

}
