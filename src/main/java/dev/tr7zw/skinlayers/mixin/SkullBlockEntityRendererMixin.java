package dev.tr7zw.skinlayers.mixin;

import static dev.tr7zw.skinlayers.SkullRendererCache.lastSkull;
import static dev.tr7zw.skinlayers.SkullRendererCache.renderNext;

import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.SkinUtil;
import dev.tr7zw.skinlayers.accessor.SkullModelAccessor;
import dev.tr7zw.skinlayers.accessor.SkullSettings;
import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.skinlayers.util.NMSWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.phys.Vec3;
// spotless:off 
//#if MC >= 11700
import net.minecraft.client.model.SkullModelBase;
//#else
//$$ import net.minecraft.world.level.block.SkullBlock;
//$$ import net.minecraft.client.model.SkullModel;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import java.util.Map;
//#endif
// spotless:on

@Mixin(SkullBlockRenderer.class)
public class SkullBlockEntityRendererMixin {

    // spotless:off 
	//#if MC <= 11605
	//$$ @Shadow
	//$$ private static Map<net.minecraft.world.level.block.SkullBlock.Type, SkullModel> MODEL_BY_TYPE;
	//$$ @Shadow
	//$$ 	private static RenderType getRenderType(net.minecraft.world.level.block.SkullBlock.Type type,
	//$$  GameProfile gameProfile) {return null;}
	//#endif
	
    @SuppressWarnings("resource")
    @Inject(method = "render", at = @At("HEAD"))
    public void render(SkullBlockEntity skullBlockEntity, float f, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int i, int j, CallbackInfo info) {
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        if (!SkinLayersModBase.config.enableSkulls)
            return;
        if (internalDistToCenterSqr(skullBlockEntity.getBlockPos(), (int) camera.x(), (int) camera.y(),
                (int) camera.z()) < SkinLayersModBase.config.renderDistanceLOD
                        * SkinLayersModBase.config.renderDistanceLOD) {
            lastSkull = (SkullSettings) skullBlockEntity;
            GameProfile gameProfile = null;
            // spotless:off 
            //#if MC <= 12004
            //$$ gameProfile = skullBlockEntity.getOwnerProfile();
            //#else
            if(skullBlockEntity.getOwnerProfile() != null) {
                gameProfile = skullBlockEntity.getOwnerProfile().gameProfile();
            }
            //#endif
            if (gameProfile == null)
                return;
            ResourceLocation textureLocation = NMSWrapper.getPlayerSkin(gameProfile);
            if (textureLocation != lastSkull.getLastTexture()) {
                lastSkull.setInitialized(false);
            }
            if (!lastSkull.initialized() && lastSkull.getHeadLayers() == null) {
                lastSkull.setInitialized(true); // do this first, so if anything goes horribly wrong, it doesn't happen
                                                // next
                // frame again
                lastSkull.setLastTexture(textureLocation);
                SkinUtil.setup3dLayers(gameProfile, lastSkull);
            }
            renderNext = lastSkull.getHeadLayers() != null;
        }
    }

    private double internalDistToCenterSqr(BlockPos pos, double d, double e, double f) {
        double g = pos.getX() + 0.5D - d;
        double h = pos.getY() + 0.5D - e;
        double i = pos.getZ() + 0.5D - f;
        return g * g + h * h + i * i;
    }

    @Inject(method = "renderSkull", at = @At("HEAD"))
    // spotless:off 
    //#if MC >= 11700
    private static void renderSkull(Direction direction, float f, float g, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int i, SkullModelBase skullModelBase, RenderType renderType,
            CallbackInfo ci) {
    	//#else
    	//$$ private static void renderSkull(Direction direction, float f,
    	//$$ net.minecraft.world.level.block.SkullBlock.Type type, GameProfile gameProfile, float g,
    	//$$ PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
    	//$$ SkullModel skullModelBase = (SkullModel) MODEL_BY_TYPE.get(type);
    	//#endif
    	// spotless:on
        if (skullModelBase instanceof SkullModelAccessor) {
            SkullModelAccessor accessor = (SkullModelAccessor) skullModelBase;
            if (!renderNext || lastSkull == null) {
                accessor.showHat(true);
                lastSkull = null;
                return;
            }
            Mesh mesh = lastSkull.getHeadLayers();
            if (mesh == null) {
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
            mesh.setPosition(0, -0.25f, 0);
            mesh.setRotation(0, f * 0.017453292F, 0);
            // spotless:off
            //#if MC <= 11605
            //$$ lastSkull.getHeadLayers().render(poseStack, multiBufferSource.getBuffer(getRenderType(type, gameProfile)), i,
            //$$ OverlayTexture.NO_OVERLAY);
            //#else
            lastSkull.getHeadLayers().render(poseStack, multiBufferSource.getBuffer(renderType), i,
                    OverlayTexture.NO_OVERLAY);
            //#endif
            //spotless:on
            poseStack.popPose();
            renderNext = false;
            lastSkull = null;
        }
    }

}
