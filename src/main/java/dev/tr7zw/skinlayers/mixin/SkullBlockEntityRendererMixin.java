package dev.tr7zw.skinlayers.mixin;

import static dev.tr7zw.skinlayers.SkullRendererCache.itemCache;
import static dev.tr7zw.skinlayers.SkullRendererCache.lastSkull;
import static dev.tr7zw.skinlayers.SkullRendererCache.renderNext;

import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.SkinUtil;
import dev.tr7zw.skinlayers.SkullRendererCache;
import dev.tr7zw.skinlayers.SkullRendererCache.ItemSettings;
import dev.tr7zw.skinlayers.accessor.SkullModelAccessor;
import dev.tr7zw.skinlayers.accessor.SkullModelStateAccessor;
import dev.tr7zw.skinlayers.accessor.SkullSettings;
import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.transition.mc.PlayerUtil;
import dev.tr7zw.transition.mc.extending.ExtensionHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.*;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.phys.Vec3;
//? if >= 1.21.11 {

import net.minecraft.client.model.object.skull.*;
//? }

//? if >= 1.17.0 {

import net.minecraft.client.model.*;
//? } else {

// import net.minecraft.world.level.block.SkullBlock;
// import net.minecraft.client.model.SkullModel;
// import org.spongepowered.asm.mixin.Shadow;
// import java.util.Map;
//? }

@Mixin(SkullBlockRenderer.class)
public class SkullBlockEntityRendererMixin {

    private static final String LAYER_HOLDER = "3d_SKIN_LAYERS_HOLDER";

    //? if >= 1.21.9 {

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void extractRenderState(SkullBlockEntity skullBlockEntity,
            net.minecraft.client.renderer.blockentity.state.SkullBlockRenderState skullBlockRenderState, float f,
            Vec3 vec3, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay,
            CallbackInfo ci) {
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera()
                /*? >= 1.21.11 {*/ .position() /*?} else {*//* .getPosition() *//*?}*/;
        if (!SkinLayersModBase.config.enableSkulls)
            return;
        if (internalDistToCenterSqr(skullBlockEntity.getBlockPos(), (int) camera.x(), (int) camera.y(),
                (int) camera.z()) < SkinLayersModBase.config.renderDistanceLOD
                        * SkinLayersModBase.config.renderDistanceLOD) {
            lastSkull = (SkullSettings) skullBlockEntity;
            GameProfile gameProfile = PlayerUtil.getProfile(skullBlockEntity.getOwnerProfile());
            if (gameProfile == null)
                return;
            var textureLocation = PlayerUtil.getPlayerSkin(gameProfile);
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
            if (renderNext && skullBlockRenderState instanceof ExtensionHolder extensionHolder) {
                extensionHolder.setExtension(LAYER_HOLDER, lastSkull);
            }
        } else {
            renderNext = false;
            lastSkull = null;
        }
    }

    @Inject(method = "submit", at = @At("HEAD"))
    private void submit(net.minecraft.client.renderer.blockentity.state.SkullBlockRenderState skullBlockRenderState,
            PoseStack poseStack, net.minecraft.client.renderer.SubmitNodeCollector submitNodeCollector,
            net.minecraft.client.renderer.state.CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (renderNext && skullBlockRenderState instanceof ExtensionHolder extensionHolder) {
            lastSkull = extensionHolder.getExtension(LAYER_HOLDER, SkullSettings.class);
        } else {
            lastSkull = null;
        }
    }

    @ModifyVariable(method = "submitSkull", at = @At("STORE"), ordinal = 0)
    private static SkullModelBase.State submitSkull(SkullModelBase.State state) {
        if (state instanceof SkullModelStateAccessor accessor) {
            accessor.setSkullSettings(lastSkull);
            lastSkull = null;
        }
        return state;
    }
    //? }

    //? if <= 1.16.5 {

    // @Shadow
    // private static Map<net.minecraft.world.level.block.SkullBlock.Type, SkullModel> MODEL_BY_TYPE;
    // @Shadow
    // 	private static RenderType getRenderType(net.minecraft.world.level.block.SkullBlock.Type type,
    //  GameProfile gameProfile) {return null;}
    //? }

    //? if < 1.21.9 {
    /*
        @SuppressWarnings("resource")
        @Inject(method = "render", at = @At("HEAD"))
        public void render(SkullBlockEntity skullBlockEntity, float f, PoseStack poseStack,
                MultiBufferSource multiBufferSource, int i, int j,
                //#if MC >= 12105
                Vec3 vec3,
                //#endif
                CallbackInfo info) {
            Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            if (!SkinLayersModBase.config.enableSkulls)
                return;
            if (internalDistToCenterSqr(skullBlockEntity.getBlockPos(), (int) camera.x(), (int) camera.y(),
                    (int) camera.z()) < SkinLayersModBase.config.renderDistanceLOD
                            * SkinLayersModBase.config.renderDistanceLOD) {
                lastSkull = (SkullSettings) skullBlockEntity;
                GameProfile gameProfile = null;
                //?if <= 1.20.4 {
                 /^gameProfile = skullBlockEntity.getOwnerProfile();
                ^///? } else {
                if (skullBlockEntity.getOwnerProfile() != null) {
                    gameProfile = PlayerUtil.getProfile(skullBlockEntity.getOwnerProfile());
                }
                //? }
                if (gameProfile == null)
                    return;
                ResourceLocation textureLocation = PlayerUtil.getPlayerSkin(gameProfile);
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
            } else {
                renderNext = false;
                lastSkull = null;
            }
        }
    *///? }

    private double internalDistToCenterSqr(BlockPos pos, double d, double e, double f) {
        double g = pos.getX() + 0.5D - d;
        double h = pos.getY() + 0.5D - e;
        double i = pos.getZ() + 0.5D - f;
        return g * g + h * h + i * i;
    }

    //? if < 1.21.9 {
    /*
        @Inject(method = "renderSkull", at = @At("HEAD"))
     //? if >= 1.17.0 {
    
         private static void renderSkull(Direction direction, float f, float g, PoseStack poseStack,
                 MultiBufferSource multiBufferSource, int i, SkullModelBase skullModelBase, RenderType renderType,
                 CallbackInfo ci) {
     //? } else {
    
     // private static void renderSkull(Direction direction, float f,
     // net.minecraft.world.level.block.SkullBlock.Type type, GameProfile gameProfile, float g,
     // PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
     // SkullModel skullModelBase = (SkullModel) MODEL_BY_TYPE.get(type);
     //? }
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
    *///? }

}
