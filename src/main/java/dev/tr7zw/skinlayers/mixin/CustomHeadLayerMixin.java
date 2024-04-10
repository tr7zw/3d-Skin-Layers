package dev.tr7zw.skinlayers.mixin;

import static dev.tr7zw.skinlayers.SkullRendererCache.itemCache;
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
import dev.tr7zw.skinlayers.SkullRendererCache.ItemSettings;
import dev.tr7zw.skinlayers.accessor.SkullSettings;
import dev.tr7zw.skinlayers.util.NMSWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;

@Mixin(CustomHeadLayer.class)
public class CustomHeadLayerMixin<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> {

    @SuppressWarnings("resource")
    @Inject(method = "render", at = @At("HEAD"))
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T livingEntity, float f,
            float g, float h, float j, float k, float l, CallbackInfo info) {
        if (!SkinLayersModBase.config.enableSkulls)
            return;
        if (Minecraft.getInstance().player != null && livingEntity
                .distanceToSqr(Minecraft.getInstance().player) > SkinLayersModBase.config.renderDistanceLOD
                        * SkinLayersModBase.config.renderDistanceLOD) {
            return; // too far away
        }
        ItemStack itemStack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
        if (itemStack.isEmpty())
            return;
        Item item = itemStack.getItem();
        if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof AbstractSkullBlock) {
            GameProfile gameProfile = NMSWrapper.getGameProfile(itemStack);
            if (gameProfile != null) {
                lastSkull = (SkullSettings) itemCache.computeIfAbsent(itemStack, it -> new ItemSettings());
                if (!lastSkull.initialized() && lastSkull.getHeadLayers() == null) {
                    SkinUtil.setup3dLayers(gameProfile, lastSkull);
                }
                renderNext = lastSkull.getHeadLayers() != null;
            }
        }
    }

}
