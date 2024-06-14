package dev.tr7zw.skinlayers.render;

import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.skinlayers.api.MeshTransformer;
import dev.tr7zw.skinlayers.api.SkinLayersAPI;
import dev.tr7zw.skinlayers.versionless.render.CustomModelPart;
import dev.tr7zw.skinlayers.versionless.render.CustomizableCube;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;

//spotless:off
//#if MC >= 11700
import net.minecraft.client.model.geom.PartPose;
//#endif
//#if MC >= 11903
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
//#else
//$$ import com.mojang.math.Vector3f;
//$$ import com.mojang.math.Vector4f;
//$$ import com.mojang.math.Matrix3f;
//$$ import com.mojang.math.Matrix4f;
//#endif
//spotless:on

/**
 * Cut down copy of the Vanilla ModelPart to bypass Optifine/Sodium screwing
 * with the CustomizableCube class
 *
 */
public class CustomizableModelPart extends CustomModelPart implements Mesh {

    private final List<Cube> cubes;
    private final Map<String, ModelPart> children;

    public CustomizableModelPart(List<Cube> list, List<CustomizableCube> customCubes, Map<String, ModelPart> map) {
        super(customCubes);
        this.cubes = list;
        this.children = map;
    }

    // spotless:off
  //#if MC >= 11700
    public void loadPose(PartPose partPose) {
        this.x = partPose.x;
        this.y = partPose.y;
        this.z = partPose.z;
        this.xRot = partPose.xRot;
        this.yRot = partPose.yRot;
        this.zRot = partPose.zRot;
    }
  //#else
    //$$ public void loadPose(ModelPart partPose){copyFrom(partPose);}
  //#endif
  //spotless:on

    public void copyFrom(ModelPart modelPart) {
        this.xRot = modelPart.xRot;
        this.yRot = modelPart.yRot;
        this.zRot = modelPart.zRot;
        this.x = modelPart.x;
        this.y = modelPart.y;
        this.z = modelPart.z;
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j) {
        render(null, poseStack, vertexConsumer, i, j, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private int convertFloatColorToInteger(float color) {
        return color > 1F ? 255 : Math.round(color * 255F);
    }

    /**
     * Kept for some mod (like ETF) shadowing the old render method to call
     */
    @Deprecated(forRemoval = true)
    public void render(ModelPart vanillaModel, PoseStack poseStack, VertexConsumer vertexConsumer, int light,
            int overlay, float red, float green, float blue, float alpha) {
        var color = (convertFloatColorToInteger(alpha) & 0xFF) << 24| (convertFloatColorToInteger(red) & 0xFF) << 16
                | (convertFloatColorToInteger(green) & 0xFF) << 8 | convertFloatColorToInteger(blue) & 0xFF;

        render(vanillaModel, poseStack, vertexConsumer, light, overlay, color);
    }

    /**
     * @param color Color, in ARGB format
     */
    public void render(ModelPart vanillaModel, PoseStack poseStack, VertexConsumer vertexConsumer, int light,
            int overlay, int color) {
        if (!this.visible)
            return;
        poseStack.pushPose();
        translateAndRotate(poseStack);
        compile(vanillaModel, poseStack.last(), vertexConsumer, light, overlay, color);
        for (ModelPart modelPart : this.children.values())
            modelPart.render(poseStack, vertexConsumer, light, overlay, color);
        poseStack.popPose();
    }

    public void translateAndRotate(PoseStack poseStack) {
        poseStack.translate(this.x / 16.0F, this.y / 16.0F, this.z / 16.0F);
        // spotless:off 
        //#if MC >= 11903
        if (this.xRot != 0.0F || this.yRot != 0.0F || this.zRot != 0.0F)
            poseStack.mulPose((new Quaternionf()).rotationZYX(this.zRot, this.yRot, this.xRot));
	    //#else
        //$$ if (this.zRot != 0.0F)
        //$$     poseStack.mulPose(Vector3f.ZP.rotation(this.zRot));
        //$$  if (this.yRot != 0.0F)
        //$$       poseStack.mulPose(Vector3f.YP.rotation(this.yRot));
        //$$   if (this.xRot != 0.0F)
        //$$      poseStack.mulPose(Vector3f.XP.rotation(this.xRot));
	    //#endif
	    //spotless:on
    }

    // render constants to reduce allocations
    private Vector4f vector4f[] = new Vector4f[] { new Vector4f(), new Vector4f(), new Vector4f(), new Vector4f() };

    private void compile(ModelPart vanillaModel, PoseStack.Pose pose, VertexConsumer vertexConsumer, int light,
            int overlay, int color) {
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

            // spotless:off 
            //#if MC >= 11903
            vector3f = matrix3f.transform(vector3f);
            for (int o = 0; o < 4; o++) {
                matrix4f.transform(vector4f[o]);
    	    //#else
            //$$    vector3f.transform(matrix3f);
            //$$   for (int o = 0; o < 4; o++) {
            //$$      vector4f[o].transform(matrix4f);
    	    //#endif
    	    //spotless:on
                vertexConsumer.addVertex(vector4f[o].x(), vector4f[o].y(), vector4f[o].z());
                vertexConsumer.setColor(color);
                vertexConsumer.setUv(polygonData[id + 3 + (o * 5) + 3], polygonData[id + 3 + (o * 5) + 4]);
                vertexConsumer.setOverlay(overlay);
                vertexConsumer.setLight(light);
                vertexConsumer.setNormal(vector3f.x(), vector3f.y(), vector3f.z());
            }
        }

        // other cubes
        for (Cube cube : this.cubes) {
            transformer.transform(cube);
            // spotless:off
            //#if MC >= 11700
            cube.compile(pose, vertexConsumer, light, overlay, color);
            //#else
			  //$$ for (ModelPart.Polygon polygon : cube.polygons) {
	          //$$ 	Vector3f vector3f = polygon.normal.copy();
	          //$$ 	vector3f.transform(matrix3f);
	          //$$ 	float l = vector3f.x();
	          //$$ 	float m = vector3f.y();
	          //$$ 	float n = vector3f.z();
	          //$$ 
	          //$$ 	for (int o = 0; o < 4; ++o) {
	          //$$ 		ModelPart.Vertex vertex = polygon.vertices[o];
	          //$$ 		float p = vertex.pos.x() / 16.0F;
	          //$$ 		float q = vertex.pos.y() / 16.0F;
	          //$$ 		float r = vertex.pos.z() / 16.0F;
	          //$$ 		Vector4f vector4f = new Vector4f(p, q, r, 1.0F);
	          //$$ 		vector4f.transform(matrix4f);
        	  //$$		vertexConsumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.u, vertex.v, overlay,
              //$$	       light, l, m, n);
	          //$$ 	}
	          //$$ }
            //#endif
            //spotless:on

        }
    }

}
