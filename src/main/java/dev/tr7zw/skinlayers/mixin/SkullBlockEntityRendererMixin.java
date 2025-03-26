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
import dev.tr7zw.util.NMSHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.phys.Vec3;
//#if MC >= 11700
import net.minecraft.client.model.SkullModelBase;
//#else
//$$ import net.minecraft.world.level.block.SkullBlock;
//$$ import net.minecraft.client.model.SkullModel;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import java.util.Map;
//#endif

@Mixin(SkullBlockRenderer.class)
public class SkullBlockEntityRendererMixin {

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
            //#if MC <= 12004
            //$$ gameProfile = skullBlockEntity.getOwnerProfile();
            //#else
            if(skullBlockEntity.getOwnerProfile() != null) {
                gameProfile = skullBlockEntity.getOwnerProfile().gameProfile();
            }
            //#endif
            if (gameProfile == null)
                return;
            ResourceLocation textureLocation = NMSHelper.getPlayerSkin(gameProfile);
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
        if (skullModelBase instanceof SkullModelAccessor accessor) {
            if (!renderNext || lastSkull == null) {
                accessor.injectHatMesh(null);
                lastSkull = null;
                return;
            }
            Mesh mesh = lastSkull.getHeadLayers();
            if (mesh == null) {
                accessor.injectHatMesh(null);
                lastSkull = null;
                return;
            }
            accessor.injectHatMesh(mesh);
            renderNext = false;
            lastSkull = null;
        }
    }

}
