package dev.tr7zw.skinlayers.mixin;

import static dev.tr7zw.skinlayers.SkullRendererCache.itemCache;
import static dev.tr7zw.skinlayers.SkullRendererCache.lastSkull;
import static dev.tr7zw.skinlayers.SkullRendererCache.renderNext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.SkinUtil;
import dev.tr7zw.skinlayers.SkullRendererCache.ItemSettings;
import dev.tr7zw.skinlayers.accessor.SkullSettings;
import dev.tr7zw.transition.mc.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.entity.LivingEntity;

//#if MC < 12104
//$$import com.mojang.authlib.GameProfile;
//$$import dev.tr7zw.skinlayers.SkinUtil;
//$$import dev.tr7zw.skinlayers.SkullRendererCache.ItemSettings;
//$$import dev.tr7zw.skinlayers.accessor.SkullSettings;
//$$import dev.tr7zw.transition.mc.ItemUtil;
//$$import net.minecraft.world.item.BlockItem;
//$$import net.minecraft.world.item.Item;
//$$import net.minecraft.world.item.ItemStack;
//$$import net.minecraft.world.level.block.AbstractSkullBlock;
//#endif

@Mixin(CustomHeadLayer.class)
public class CustomHeadLayerMixin<T extends LivingEntity, M extends EntityModel & HeadedModel> {

    //#if MC >= 12109
    @Inject(method = "resolveSkullRenderType", at = @At("HEAD"))
    private void resolveSkullRenderType(
            net.minecraft.client.renderer.entity.state.LivingEntityRenderState livingEntityRenderState,
            net.minecraft.world.level.block.SkullBlock.Type type, CallbackInfoReturnable<RenderType> ci) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().gameRenderer.getMainCamera().getPosition()
                .distanceToSqr(livingEntityRenderState.x, livingEntityRenderState.y,
                        livingEntityRenderState.z) > SkinLayersModBase.config.renderDistanceLOD
                                * SkinLayersModBase.config.renderDistanceLOD) {
            return; // too far away
        }
        if ((!livingEntityRenderState.headItem.isEmpty() || livingEntityRenderState.wornHeadType != null)
                && livingEntityRenderState.wornHeadProfile != null) {
            GameProfile gameProfile = PlayerUtil.getProfile(livingEntityRenderState.wornHeadProfile);
            lastSkull = (SkullSettings) itemCache.computeIfAbsent(gameProfile, it -> new ItemSettings());
            if (!lastSkull.initialized() && lastSkull.getHeadLayers() == null) {
                SkinUtil.setup3dLayers(gameProfile, lastSkull);
            }
            renderNext = lastSkull.getHeadLayers() != null;
        }
    }
    //#elseif MC >= 12102
    //$$@SuppressWarnings("resource")
    //$$@Inject(method = "render", at = @At("HEAD"))
    //$$public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i,
    //$$        net.minecraft.client.renderer.entity.state.LivingEntityRenderState livingEntityRenderState, float f,
    //$$        float g, CallbackInfo info) {
    //$$
    //$$    if (Minecraft.getInstance().player != null && Minecraft.getInstance().gameRenderer.getMainCamera().getPosition()
    //$$            .distanceToSqr(livingEntityRenderState.x, livingEntityRenderState.y,
    //$$                    livingEntityRenderState.z) > SkinLayersModBase.config.renderDistanceLOD
    //$$                            * SkinLayersModBase.config.renderDistanceLOD) {
    //$$        return; // too far away
    //$$     }
    //#if MC >= 12104
    //$$    if ((!livingEntityRenderState.headItem.isEmpty() || livingEntityRenderState.wornHeadType != null)
    //$$             && livingEntityRenderState.wornHeadProfile != null
    //$$             && livingEntityRenderState.wornHeadProfile.isResolved()) {
    //$$         GameProfile gameProfile = livingEntityRenderState.wornHeadProfile.gameProfile();
    //$$         lastSkull = (SkullSettings) itemCache.computeIfAbsent(gameProfile, it -> new ItemSettings());
    //$$         if (!lastSkull.initialized() && lastSkull.getHeadLayers() == null) {
    //$$            SkinUtil.setup3dLayers(gameProfile, lastSkull);
    //$$        }
    //$$        renderNext = lastSkull.getHeadLayers() != null;
    //$$    }
    //#else
    //$$setupHeadRendering(livingEntityRenderState.headItem);
    //#endif
    //$$}
    //#else
    //$$ @SuppressWarnings("resource")
    //$$ @Inject(method = "render", at = @At("HEAD"))
    //$$ public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T livingEntity, float f,
    //$$          float g, float h, float j, float k, float l, CallbackInfo info) {
    //$$      if (Minecraft.getInstance().player != null && livingEntity.distanceToSqr(Minecraft.getInstance().gameRenderer
    //$$             .getMainCamera().getPosition()) > SkinLayersModBase.config.renderDistanceLOD
    //$$                      * SkinLayersModBase.config.renderDistanceLOD) {
    //$$         return; // too far away
    //$$      }
    //$$      setupHeadRendering(livingEntity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD));
    //$$  }
    //#endif

    //#if MC < 12104
    //$$private void setupHeadRendering(ItemStack itemStack) {
    //$$    if (!SkinLayersModBase.config.enableSkulls || itemStack.isEmpty())
    //$$        return;
    //$$    Item item = itemStack.getItem();
    //$$    if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof AbstractSkullBlock) {
    //$$        GameProfile gameProfile = ItemUtil.getGameProfile(itemStack);
    //$$        if (gameProfile != null) {
    //$$            lastSkull = (SkullSettings) itemCache.computeIfAbsent(itemStack, it -> new ItemSettings());
    //$$            if (!lastSkull.initialized() && lastSkull.getHeadLayers() == null) {
    //$$                SkinUtil.setup3dLayers(gameProfile, lastSkull);
    //$$            }
    //$$            renderNext = lastSkull.getHeadLayers() != null;
    //$$         }
    //$$    }
    //$$}
    //#endif

}
