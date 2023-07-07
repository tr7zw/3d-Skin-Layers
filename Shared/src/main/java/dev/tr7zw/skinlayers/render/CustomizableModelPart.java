package dev.tr7zw.skinlayers.render;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.skinlayers.api.MeshTransformer;
import dev.tr7zw.skinlayers.api.SkinLayersAPI;
import dev.tr7zw.skinlayers.render.CustomizableCube.Polygon;
import dev.tr7zw.skinlayers.render.CustomizableCube.Vertex;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.model.geom.PartPose;

/**
 * Cut down copy of the Vanilla ModelPart to bypass Optifine/Sodium screwing
 * with the CustomizableCube class
 *
 */
class CustomizableModelPart implements Mesh {

    private float x;
    private float y;
    private float z;
    private float xRot;
    private float yRot;
    private float zRot;
    private boolean visible = true;
    private final List<Cube> cubes;
    private final Map<String, ModelPart> children;
    private float[] polygonData = null;
    private int polygonAmount = 0;
    private final int polyDataSize = 23;

    public CustomizableModelPart(List<Cube> list, List<CustomizableCube> customCubes, Map<String, ModelPart> map) {
        this.cubes = list;
        this.children = map;
        compactCubes(customCubes);
    }

    private void compactCubes(List<CustomizableCube> customCubes) {
        for (CustomizableCube cube : customCubes) {
            polygonAmount += cube.polygonCount;
        }
        polygonData = new float[polygonAmount * polyDataSize];
        int offset = 0;
        Polygon polygon;
        for (CustomizableCube cube : customCubes) {
            for (int id = 0; id < cube.polygonCount; id++) {
                polygon = cube.polygons[id];
                Vector3f vector3f = polygon.normal;
                polygonData[offset + 0] = vector3f.x();
                polygonData[offset + 1] = vector3f.y();
                polygonData[offset + 2] = vector3f.z();
                for (int i = 0; i < 4; i++) {
                    Vertex vertex = polygon.vertices[i];
                    polygonData[offset + 3 + (i * 5) + 0] = vertex.scaledX;
                    polygonData[offset + 3 + (i * 5) + 1] = vertex.scaledY;
                    polygonData[offset + 3 + (i * 5) + 2] = vertex.scaledZ;
                    polygonData[offset + 3 + (i * 5) + 3] = vertex.u;
                    polygonData[offset + 3 + (i * 5) + 4] = vertex.v;
                }
                offset += polyDataSize;
            }
        }
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

    public void setPosition(float f, float g, float h) {
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
        render(null, poseStack, vertexConsumer, i, j, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void render(ModelPart vanillaModel, PoseStack poseStack, VertexConsumer vertexConsumer, int light,
            int overlay, float red, float green, float blue, float alpha) {
        if (!this.visible)
            return;
        poseStack.pushPose();
        translateAndRotate(poseStack);
        compile(vanillaModel, poseStack.last(), vertexConsumer, light, overlay, red, green, blue, alpha);
        for (ModelPart modelPart : this.children.values())
            modelPart.render(poseStack, vertexConsumer, light, overlay, red, green, blue, alpha);
        poseStack.popPose();
    }

    public void translateAndRotate(PoseStack poseStack) {
        poseStack.translate(this.x / 16.0F, this.y / 16.0F, this.z / 16.0F);
        if (this.xRot != 0.0F || this.yRot != 0.0F || this.zRot != 0.0F)
            poseStack.mulPose((new Quaternionf()).rotationZYX(this.zRot, this.yRot, this.xRot));
    }

    // render constants to reduce allocations
    private Vector4f vector4f[] = new Vector4f[] { new Vector4f(), new Vector4f(), new Vector4f(), new Vector4f() };

    private void compile(ModelPart vanillaModel, PoseStack.Pose pose, VertexConsumer vertexConsumer, int light,
            int overlay, float red, float green, float blue, float alpha) {
        MeshTransformer transformer = SkinLayersAPI.getMeshTransformerProvider().prepareTransformer(vanillaModel);
        // compacted Cubes
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        for (int id = 0; id < polygonData.length; id += polyDataSize) {
            Vector3f vector3f = new Vector3f(polygonData[id + 0], polygonData[id + 1], polygonData[id + 2]);
            for (int o = 0; o < 4; o++) {
                vector4f[o].set(polygonData[id + 3 + (o * 5) + 0], polygonData[id + 3 + (o * 5) + 1],
                        polygonData[id + 3 + (o * 5) + 2], 1.0F);
            }
            // optional transformations for bending layers
            transformer.transform(vector3f, vector4f);

            vector3f = matrix3f.transform(vector3f);
            for (int o = 0; o < 4; o++) {
                matrix4f.transform(vector4f[o]);
                vertexConsumer.vertex(vector4f[o].x(), vector4f[o].y(), vector4f[o].z(), red, green, blue, alpha,
                        polygonData[id + 3 + (o * 5) + 3], polygonData[id + 3 + (o * 5) + 4], overlay, light,
                        vector3f.x(), vector3f.y(), vector3f.z());
            }
        }

        // other cubes
        for (Cube cube : this.cubes) {
            transformer.transform(cube);
            cube.compile(pose, vertexConsumer, light, overlay, red, green, blue, alpha);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

}
