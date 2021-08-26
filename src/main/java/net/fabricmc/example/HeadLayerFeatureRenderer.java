package net.fabricmc.example;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class HeadLayerFeatureRenderer
		extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

	public HeadLayerFeatureRenderer(
            RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
        super(renderLayerParent);
    }

    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i,
            AbstractClientPlayer player, float f, float g, float h, float j, float k,
			float l) {
		if (!player.isSkinLoaded() || player.isInvisible()) {
			return;
		}
		ItemStack itemStack = player.getItemBySlot(EquipmentSlot.HEAD);
		if (itemStack != null && ((itemStack.getItem() instanceof BlockItem))) {
			return;
		}
		// Check firstperson?
		
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
		settings.setupHeadLayers(SolidPixelWrapper.wrapBoxOptimized(skin, this.getParentModel(), 8, 8, 8, 32, 0, false, 0));
		skin.untrack();
		return true;
	}

	public void renderCustomHelmet(PlayerSettings settings, AbstractClientPlayer abstractClientPlayer, PoseStack matrixStack, VertexConsumer vertices, int light, int overlay) {
		if(settings.getHeadLayers() == null)return;
		if(!this.getParentModel().head.visible || !abstractClientPlayer.isModelPartShown(PlayerModelPart.HAT))return;
		matrixStack.pushPose();
		this.getParentModel().head.translateAndRotate(matrixStack);
		matrixStack.scale(1.18f, 1.18f, 1.18f); // 1.18
		((ModelPart)settings.getHeadLayers()).render(matrixStack, vertices, light, overlay);
		matrixStack.popPose();

	}
	

}
