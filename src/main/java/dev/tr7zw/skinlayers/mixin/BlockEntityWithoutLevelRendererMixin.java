package dev.tr7zw.skinlayers.mixin;

import static dev.tr7zw.skinlayers.SkullRendererCache.lastSkull;
import static dev.tr7zw.skinlayers.SkullRendererCache.renderNext;
import static dev.tr7zw.skinlayers.SkullRendererCache.itemCache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.tr7zw.skinlayers.SkinUtil;
import dev.tr7zw.skinlayers.SkullRendererCache.ItemSettings;
import dev.tr7zw.skinlayers.accessor.SkullSettings;
import dev.tr7zw.transition.mc.ItemUtil;
import net.minecraft.world.item.ItemStack;

//#if MC < 12106
//$$import com.mojang.authlib.GameProfile;
//$$import com.mojang.blaze3d.vertex.PoseStack;
//$$import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$import dev.tr7zw.skinlayers.SkinLayersModBase;
//$$import dev.tr7zw.skinlayers.SkullRendererCache.ItemSettings;
//$$import net.minecraft.client.renderer.MultiBufferSource;
//$$
//#if MC >= 11904
//$$import net.minecraft.world.item.ItemDisplayContext;
//#else
//$$ import net.minecraft.client.renderer.block.model.ItemTransforms;
//#endif
//#if MC >= 12104
//$$import net.minecraft.client.renderer.special.SkullSpecialRenderer;
//$$import net.minecraft.world.item.component.ResolvableProfile;
//#endif
//#endif

//#if MC >= 12106
@Mixin(net.minecraft.client.renderer.special.PlayerHeadSpecialRenderer.class)
public class BlockEntityWithoutLevelRendererMixin {
    @Inject(method = "extractArgument", at = @At("HEAD"))
    public void extractArgument(ItemStack itemStack,
            CallbackInfoReturnable<net.minecraft.client.renderer.special.PlayerHeadSpecialRenderer.PlayerHeadRenderInfo> cir) {
        var profile = ItemUtil.getGameProfile(itemStack);
        if (profile != null) {
            lastSkull = (SkullSettings) itemCache.computeIfAbsent(profile, it -> new ItemSettings());
            if (!lastSkull.initialized() && lastSkull.getHeadLayers() == null) {
                SkinUtil.setup3dLayers(profile, lastSkull);
            }
            renderNext = lastSkull.getHeadLayers() != null;
        }
    }
}
//#elseif MC >= 12104
//$$@Mixin(net.minecraft.client.renderer.special.SkullSpecialRenderer.class)
//$$public class BlockEntityWithoutLevelRendererMixin {
//$$ @Inject(method = "render", at = @At("HEAD"))
//$$ public void render(ResolvableProfile resolvableProfile, ItemDisplayContext itemDisplayContext, PoseStack poseStack,
//$$         MultiBufferSource multiBufferSource, int i, int j, boolean bl, CallbackInfo ci) {
//$$     if (SkinLayersModBase.config.enableSkullsItems && resolvableProfile != null && resolvableProfile.isResolved()) {
//$$        GameProfile gameProfile = resolvableProfile.gameProfile();
//$$         if (gameProfile != null) {
//$$             lastSkull = (SkullSettings) itemCache.computeIfAbsent(gameProfile, it -> new ItemSettings());
//$$             if (!lastSkull.initialized() && lastSkull.getHeadLayers() == null) {
//$$                 SkinUtil.setup3dLayers(gameProfile, lastSkull);
//$$             }
//$$             renderNext = lastSkull.getHeadLayers() != null;
//$$         }
//$$      }
//$$  }
//$$}
//#else
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
//$$    //#if MC >= 11904
//$$    public void renderByItem(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack,
//$$            MultiBufferSource multiBufferSource, int i, int j, CallbackInfo info) {
//$$    //#else
//$$    //$$    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack poseStack,
//$$    //$$            MultiBufferSource multiBufferSource, int i, int j, CallbackInfo info) {
//$$    //#endif
//$$        if (!SkinLayersModBase.config.enableSkullsItems)
//$$            return;
//$$        Item item = itemStack.getItem();
//$$        if (item instanceof BlockItem) {
//$$            Block block = ((BlockItem) item).getBlock();
//$$            if (block instanceof AbstractSkullBlock) {
//$$                GameProfile gameProfile = ItemUtil.getGameProfile(itemStack);
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
