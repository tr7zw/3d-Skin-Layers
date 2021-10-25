package dev.tr7zw.skinlayers.renderlayers;

import java.util.Set;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.SkinUtil;
import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.render.SolidPixelWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HeadLayerFeatureRenderer
		extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

	public HeadLayerFeatureRenderer(
            RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
        super(renderLayerParent);
    }

	private Set<Item> hideHeadLayers = Sets.newHashSet(Items.PLAYER_HEAD, Items.ZOMBIE_HEAD, Items.CREEPER_HEAD, Items.DRAGON_HEAD, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL);
	
	private static final Minecraft mc = Minecraft.getInstance();
	
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i,
            AbstractClientPlayer player, float f, float g, float h, float j, float k,
			float l) {
		if (!player.isSkinLoaded() || player.isInvisible() || !SkinLayersModBase.config.enableHat) {
			return;
		}
		if(mc.player.distanceToSqr(player) > SkinLayersModBase.config.renderDistanceLOD*SkinLayersModBase.config.renderDistanceLOD)return;
		
		ItemStack itemStack = player.getItemBySlot(EquipmentSlot.HEAD);
		if (itemStack != null && hideHeadLayers.contains(itemStack.getItem())) {
			return;
		}
		
		PlayerSettings settings = (PlayerSettings) player;
		// check for it being setup first to speedup the rendering
		if(settings.getHeadLayers() == null && !setupModel(player, settings)) {
			return; // no head layer setup and wasn't able to setup
		}

		VertexConsumer vertexConsumer = multiBufferSource
				.getBuffer(RenderType.entityTranslucentCull((ResourceLocation) player.getSkinTextureLocation()));
		int m = LivingEntityRenderer.getOverlayCoords((LivingEntity) player, (float) 0.0f);
		renderCustomHelmet(settings, player, poseStack, vertexConsumer, i, m);
	}

	private boolean setupModel(AbstractClientPlayer abstractClientPlayerEntity, PlayerSettings settings) {
		
		if(!SkinUtil.hasCustomSkin(abstractClientPlayerEntity)) {
			return false; // default skin
		}
		NativeImage skin = SkinUtil.getSkinTexture(abstractClientPlayerEntity);
		settings.setupHeadLayers(SolidPixelWrapper.wrapBoxOptimized(skin, 8, 8, 8, 32, 0, false, 0.6f));
		skin.untrack();
		return true;
	}

	public void renderCustomHelmet(PlayerSettings settings, AbstractClientPlayer abstractClientPlayer, PoseStack matrixStack, VertexConsumer vertices, int light, int overlay) {
		if(settings.getHeadLayers() == null)return;
		if(!this.getParentModel().head.visible || !abstractClientPlayer.isModelPartShown(PlayerModelPart.HAT))return;
		float voxelSize = SkinLayersModBase.config.headVoxelSize;
		matrixStack.pushPose();
		this.getParentModel().head.translateAndRotate(matrixStack);
		matrixStack.scale(voxelSize, voxelSize, voxelSize);
		// Overlay refuses to work correctly, this is a workaround for now
		boolean red = abstractClientPlayer.hurtTime > 0 || abstractClientPlayer.deathTime > 0;
		float color = red ? 0.5f : 1f;
		settings.getHeadLayers().render(matrixStack, vertices, light, overlay, 1.0f, color, color, 1.0f);
		matrixStack.popPose();

	}
	

}
