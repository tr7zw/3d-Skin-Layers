package dev.tr7zw.skinlayers.render;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;

/**
 * Providing the old inventory player render logic
 *
 */
public class PreviewHelper {

    public static void renderEntityInInventoryFollowsMouse(GuiGraphics guiGraphics, int x, int y, int scale,
            float mouseX, float mouseY, LivingEntity entity) {
        float f = (float) Math.atan((double) (mouseX / 40.0F));
        float f1 = (float) Math.atan((double) (mouseY / 40.0F));
        renderEntityInInventoryFollowsAngle(guiGraphics, x, y, scale, f, f1, entity);
    }

    public static void renderEntityInInventoryFollowsAngle(GuiGraphics pGuiGraphics, int pX, int pY, int pScale,
            float angleXComponent, float angleYComponent, LivingEntity pEntity) {
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
        Quaternionf quaternionf1 = (new Quaternionf()).rotateX(angleYComponent * 20.0F * 0.017453292F);
        quaternionf.mul(quaternionf1);
        float f2 = pEntity.yBodyRot;
        float f3 = pEntity.getYRot();
        float f4 = pEntity.getXRot();
        float f5 = pEntity.yHeadRotO;
        float f6 = pEntity.yHeadRot;
        pEntity.yBodyRot = 180.0F + angleXComponent * 20.0F;
        pEntity.setYRot(180.0F + angleXComponent * 40.0F);
        pEntity.setXRot(-angleYComponent * 20.0F);
        pEntity.yHeadRot = pEntity.getYRot();
        pEntity.yHeadRotO = pEntity.getYRot();
        renderEntityInInventory(pGuiGraphics, pX, pY, pScale, quaternionf, quaternionf1, pEntity);
        pEntity.yBodyRot = f2;
        pEntity.setYRot(f3);
        pEntity.setXRot(f4);
        pEntity.yHeadRotO = f5;
        pEntity.yHeadRot = f6;
    }

    public static void renderEntityInInventory(GuiGraphics guiGraphics, int x, int y, int scale, Quaternionf pose,
            @Nullable Quaternionf cameraOrientation, LivingEntity entity) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate((double) x, (double) y, 50.0D);
        guiGraphics.pose().mulPoseMatrix((new Matrix4f()).scaling((float) scale, (float) scale, (float) (-scale)));
        guiGraphics.pose().mulPose(pose);
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        if (cameraOrientation != null) {
            cameraOrientation.conjugate();
            entityrenderdispatcher.overrideCameraOrientation(cameraOrientation);
        }

        entityrenderdispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> entityrenderdispatcher.render(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f,
                guiGraphics.pose(), guiGraphics.bufferSource(), 15728880));
        guiGraphics.flush();
        entityrenderdispatcher.setRenderShadow(true);
        guiGraphics.pose().popPose();
        Lighting.setupFor3DItems();
    }

}
