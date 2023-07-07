package dev.tr7zw.skinlayers.mixin;

import static dev.tr7zw.skinlayers.SkullRendererCache.itemCache;
import static dev.tr7zw.skinlayers.SkullRendererCache.lastSkull;
import static dev.tr7zw.skinlayers.SkullRendererCache.renderNext;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.SkinUtil;
import dev.tr7zw.skinlayers.SkullRendererCache.ItemSettings;
import dev.tr7zw.skinlayers.accessor.SkullSettings;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class BlockEntityWithoutLevelRendererMixin {

    @Shadow
    private Map<SkullBlock.Type, SkullModelBase> skullModels;

    @Inject(method = "renderByItem", at = @At("HEAD"))
    public void renderByItem(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int i, int j, CallbackInfo info) {
        if (!SkinLayersModBase.config.enableSkullsItems)
            return;
        Item item = itemStack.getItem();
        if (item instanceof BlockItem) {
            Block block = ((BlockItem) item).getBlock();
            if (block instanceof AbstractSkullBlock) {
                GameProfile gameProfile = null;
                if (itemStack.hasTag()) {
                    CompoundTag compoundTag = itemStack.getTag();
                    if (compoundTag.contains("CustomModelData")) {
                        return; // do not try to 3d-fy custom head models
                    }
                    if (compoundTag.contains("SkullOwner", 10)) {
                        gameProfile = NbtUtils.readGameProfile(compoundTag.getCompound("SkullOwner"));
                    } else if (compoundTag.contains("SkullOwner", 8)
                            && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
                        gameProfile = new GameProfile(null, compoundTag.getString("SkullOwner"));
                        compoundTag.remove("SkullOwner");
                        SkullBlockEntity.updateGameprofile(gameProfile, (gp) -> compoundTag.put("SkullOwner",
                                NbtUtils.writeGameProfile(new CompoundTag(), gp)));
                    }
                }
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

}
