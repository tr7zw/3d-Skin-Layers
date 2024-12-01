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
import net.minecraft.client.renderer.MultiBufferSource;

//#if MC >= 11904
import net.minecraft.world.item.ItemDisplayContext;
//#else
//$$ import net.minecraft.client.renderer.block.model.ItemTransforms;
//#endif
//#if MC >= 12104
import net.minecraft.client.renderer.special.SkullSpecialRenderer;
import net.minecraft.world.item.component.ResolvableProfile;

@Mixin(SkullSpecialRenderer.class)
public class BlockEntityWithoutLevelRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    public void render(ResolvableProfile resolvableProfile, ItemDisplayContext itemDisplayContext, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int i, int j, boolean bl, CallbackInfo ci) {
        if (SkinLayersModBase.config.enableSkullsItems && resolvableProfile.isResolved()) {
            GameProfile gameProfile = resolvableProfile.gameProfile();
            if (gameProfile != null) {
                lastSkull = (SkullSettings) itemCache.computeIfAbsent(gameProfile, it -> new ItemSettings());
                if (!lastSkull.initialized() && lastSkull.getHeadLayers() == null) {
                    SkinUtil.setup3dLayers(gameProfile, lastSkull);
                }
                renderNext = lastSkull.getHeadLayers() != null;
            }
        }
    }

}
//#else
//$$import dev.tr7zw.util.NMSHelper;
//$$import net.minecraft.world.item.BlockItem;
//$$import net.minecraft.world.item.Item;
//$$
//$$import net.minecraft.world.item.ItemStack;
//$$import net.minecraft.world.level.block.AbstractSkullBlock;
//$$import net.minecraft.world.level.block.Block;
//$$import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
//$$@Mixin(BlockEntityWithoutLevelRenderer.class)
//$$public class BlockEntityWithoutLevelRendererMixin {
//$$
//$$    @Inject(method = "renderByItem", at = @At("HEAD"))
//$$    // spotless:off 
//$$    //#if MC >= 11904
//$$    public void renderByItem(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack,
//$$            MultiBufferSource multiBufferSource, int i, int j, CallbackInfo info) {
//$$	//#else
//$$    //$$	public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack poseStack,
//$$    //$$            MultiBufferSource multiBufferSource, int i, int j, CallbackInfo info) {
//$$	//#endif
//$$	// spotless:on
//$$        if (!SkinLayersModBase.config.enableSkullsItems)
//$$            return;
//$$        Item item = itemStack.getItem();
//$$        if (item instanceof BlockItem) {
//$$            Block block = ((BlockItem) item).getBlock();
//$$            if (block instanceof AbstractSkullBlock) {
//$$                GameProfile gameProfile = NMSHelper.getGameProfile(itemStack);
//$$                if (gameProfile != null) {
//$$                    lastSkull = (SkullSettings) itemCache.computeIfAbsent(itemStack, it -> new ItemSettings());
//$$                    if (!lastSkull.initialized() && lastSkull.getHeadLayers() == null) {
//$$                        SkinUtil.setup3dLayers(gameProfile, lastSkull);
//$$                    }
//$$                    renderNext = lastSkull.getHeadLayers() != null;
//$$                }
//$$            }
//$$        }
//$$    }
//$$
//$$}
//#endif
