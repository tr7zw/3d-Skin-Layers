package dev.tr7zw.skinlayers.mixin;

import static dev.tr7zw.skinlayers.SkullRendererCache.lastSkull;
import static dev.tr7zw.skinlayers.SkullRendererCache.renderNext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.SkinUtil;
import dev.tr7zw.skinlayers.accessor.SkullModelAccessor;
import dev.tr7zw.skinlayers.accessor.SkullSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

@Mixin(SkullBlockRenderer.class)
public class SkullBlockEntityRendererMixin {
    
    @Inject(method = "render", at = @At("HEAD"))
    public void render(SkullBlockEntity skullBlockEntity, float f, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int i, int j, CallbackInfo info) {
        LocalPlayer player = Minecraft.getInstance().player;
        if(!SkinLayersModBase.config.enableSkulls)return;
        if(skullBlockEntity.getBlockPos().distSqr((int)player.getX(), (int)player.getY(), (int)player.getZ(), true) < SkinLayersModBase.config.renderDistanceLOD*SkinLayersModBase.config.renderDistanceLOD) {
            lastSkull = (SkullSettings) skullBlockEntity;
            if(lastSkull.getHeadLayers() == null) {
                SkinUtil.setup3dLayers(skullBlockEntity.getOwnerProfile(), lastSkull);
            }
            renderNext = lastSkull.getHeadLayers() != null;
        }
    }
    
    @Inject(method = "renderSkull", at = @At("HEAD"))
    private static void renderSkull(Direction direction, float f, float g, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int i, SkullModelBase skullModelBase, RenderType renderType, CallbackInfo ci) {
        if(skullModelBase instanceof SkullModelAccessor) {
            SkullModelAccessor accessor = (SkullModelAccessor) skullModelBase;
            if(!renderNext) {
                accessor.showHat(true);
                lastSkull = null;
                return;
            }
            accessor.showHat(false);
            
            poseStack.pushPose();
            if (direction == null) {
                poseStack.translate(0.5D, 0.0D, 0.5D);
            } else {
                float h = 0.25F;
                poseStack.translate((0.5F - direction.getStepX() * h), h, (0.5F - direction.getStepZ() * h));
            }
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            float voxelSize = SkinLayersModBase.config.skullVoxelSize;
            poseStack.scale(voxelSize, voxelSize, voxelSize);
            lastSkull.getHeadLayers().yRot = f * 0.017453292F;
            //lastSkull.getHeadLayers().xRot = g * 0.017453292F;
            lastSkull.getHeadLayers().render(poseStack, multiBufferSource.getBuffer(renderType), i, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
            renderNext = false;
            lastSkull = null;
        }
    }
    
}
