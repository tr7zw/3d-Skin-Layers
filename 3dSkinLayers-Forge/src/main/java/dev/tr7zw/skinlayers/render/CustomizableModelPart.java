package dev.tr7zw.skinlayers.render;

import java.util.List;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;

/**
 * Cut down copy of the Vanilla ModelPart to bypass Optifine/Sodium screwing
 * with the CustomizableCube class
 *
 */
public class CustomizableModelPart {

    public float x;
    public float y;
    public float z;
    public boolean visible = true;
    private final List<CustomizableCube> cubes;

    public CustomizableModelPart(List<CustomizableCube> list) {
        this.cubes = list;
    }

    public void copyFrom(ModelBox modelPart) {
        this.x = modelPart.posX1;
        this.y = modelPart.posY1;
        this.z = modelPart.posZ1;
    }

    public void setPos(float f, float g, float h) {
        this.x = f;
        this.y = g;
        this.z = h;
    }


    public void render(boolean redTint) {
        if (!this.visible)
            return;
        GlStateManager.pushMatrix();
        translateAndRotate();
        compile(redTint);
        GlStateManager.popMatrix();
    }

    public void translateAndRotate() {
        GlStateManager.translate((this.x / 16.0F), (this.y / 16.0F), (this.z / 16.0F));
    }

    private void compile(boolean redTint) {
        for (CustomizableCube cube : this.cubes)
            cube.render(Tessellator.getInstance(), redTint);
    }

}
