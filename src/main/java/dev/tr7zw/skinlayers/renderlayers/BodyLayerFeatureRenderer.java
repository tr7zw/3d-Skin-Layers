package dev.tr7zw.skinlayers.renderlayers;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.skinlayers.SkinLayersMod;
import dev.tr7zw.skinlayers.SkinUtil;
import dev.tr7zw.skinlayers.accessor.PlayerEntityModelAccessor;
import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.render.CustomizableModelPart;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;

public class BodyLayerFeatureRenderer 
extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
	public BodyLayerFeatureRenderer(
	        RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
			super(renderLayerParent);
			thinArms = ((PlayerEntityModelAccessor)getParentModel()).hasThinArms();
	}

	private final boolean thinArms;

	private static final Minecraft mc = Minecraft.getInstance();
	
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i,
            AbstractClientPlayer player, float f, float g, float h, float j, float k,
            float l) {
        if (!player.isSkinLoaded() || player.isInvisible()) {
            return;
        }
        if(mc.player.distanceToSqr(player) > SkinLayersMod.config.renderDistanceLOD*SkinLayersMod.config.renderDistanceLOD)return;

		PlayerSettings settings = (PlayerSettings) player;
		// check for it being setup first to speedup the rendering
		if(settings.getSkinLayers() == null && !setupModel(player, settings)) {
			return; // no head layer setup and wasn't able to setup
		}
		
		VertexConsumer vertexConsumer = multiBufferSource
				.getBuffer(RenderType.entityTranslucentCull((ResourceLocation) player.getSkinTextureLocation()));
		int m = LivingEntityRenderer.getOverlayCoords((LivingEntity) player, (float) 0.0f);
		renderLayers(player, (CustomizableModelPart[]) settings.getSkinLayers(), poseStack, vertexConsumer, i, m);
	}
	
	private boolean setupModel(AbstractClientPlayer abstractClientPlayerEntity, PlayerSettings settings) {
		if(!SkinUtil.hasCustomSkin(abstractClientPlayerEntity)) {
			return false; // default skin
		}
		NativeImage skin = SkinUtil.getSkinTexture(abstractClientPlayerEntity);
		CustomizableModelPart[] layers = new CustomizableModelPart[5];
		layers[0] = SolidPixelWrapper.wrapBoxOptimized(skin, this.getParentModel(), 4, 12, 4, 0, 48, true, -1f);
		layers[1] = SolidPixelWrapper.wrapBoxOptimized(skin, this.getParentModel(), 4, 12, 4, 0, 32, true, -1f);
		if(thinArms) {
			layers[2] = SolidPixelWrapper.wrapBoxOptimized(skin, this.getParentModel(), 3, 12, 4, 48, 48, true, -2.1f);
			layers[3] = SolidPixelWrapper.wrapBoxOptimized(skin, this.getParentModel(), 3, 12, 4, 40, 32, true, -2.1f);
		} else {
			layers[2] = SolidPixelWrapper.wrapBoxOptimized(skin, this.getParentModel(), 4, 12, 4, 48, 48, true, -2.1f);
			layers[3] = SolidPixelWrapper.wrapBoxOptimized(skin, this.getParentModel(), 4, 12, 4, 40, 32, true, -2.1f);
		}
		layers[4] = SolidPixelWrapper.wrapBoxOptimized(skin, this.getParentModel(), 8, 12, 4, 16, 32, true, 0);
		settings.setupSkinLayers(layers);
		skin.untrack();
		return true;
	}

	public void renderLayers(AbstractClientPlayer abstractClientPlayer, CustomizableModelPart[] layers, PoseStack matrixStack, VertexConsumer vertices, int light, int overlay) {
		if(layers == null)return;
		float pixelScaling = SkinLayersMod.config.baseVoxelSize;
		float heightScaling = SkinLayersMod.config.bodyVoxelHeightSize;
		CustomizableModelPart leftLeg = layers[0];
		CustomizableModelPart rightLeg = layers[1];
		CustomizableModelPart leftArm = layers[2];
		CustomizableModelPart rightArm = layers[3];
		CustomizableModelPart jacket = layers[4];
		// Overlay refuses to work correctly, this is a workaround for now
	    boolean red = abstractClientPlayer.hurtTime > 0 || abstractClientPlayer.deathTime > 0;
	    float color = red ? 0.5f : 1f;
		// Left leg
		if(abstractClientPlayer.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG) && this.getParentModel().leftLeg.visible && SkinLayersMod.config.enableLeftPants) {
			matrixStack.pushPose();
			this.getParentModel().leftLeg.translateAndRotate(matrixStack);
			matrixStack.scale(pixelScaling, heightScaling, pixelScaling);
			leftLeg.render(matrixStack, vertices, light, overlay, 1.0f, color, color, 1.0f);
			matrixStack.popPose();
		}
		// Right leg
		if(abstractClientPlayer.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG) && this.getParentModel().rightLeg.visible && SkinLayersMod.config.enableRightPants) {
			matrixStack.pushPose();
			this.getParentModel().rightLeg.translateAndRotate(matrixStack);
			matrixStack.scale(pixelScaling, heightScaling, pixelScaling);
			rightLeg.render(matrixStack, vertices, light, overlay, 1.0f, color, color, 1.0f);
			matrixStack.popPose();
		}
		// Left Arm
		if(abstractClientPlayer.isModelPartShown(PlayerModelPart.LEFT_SLEEVE) && this.getParentModel().leftArm.visible && SkinLayersMod.config.enableLeftSleeve) {
			matrixStack.pushPose();
			this.getParentModel().leftArm.translateAndRotate(matrixStack);
			leftArm.x = thinArms ? 0.6f: 1f;
			matrixStack.scale(pixelScaling, heightScaling, pixelScaling);
			leftArm.render(matrixStack, vertices, light, overlay, 1.0f, color, color, 1.0f);
			matrixStack.popPose();
		}
		// Right Arm
		if(abstractClientPlayer.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE) && this.getParentModel().rightArm.visible && SkinLayersMod.config.enableRightSleeve) {
			matrixStack.pushPose();
			this.getParentModel().rightArm.translateAndRotate(matrixStack);
			rightArm.x = thinArms ? -0.6f: -1f;
			matrixStack.scale(pixelScaling, heightScaling, pixelScaling);
			rightArm.render(matrixStack, vertices, light, overlay, 1.0f, color, color, 1.0f);
			matrixStack.popPose();
		}
		// jacket
		if(abstractClientPlayer.isModelPartShown(PlayerModelPart.JACKET) && this.getParentModel().body.visible && SkinLayersMod.config.enableJacket) {
			matrixStack.pushPose();
			jacket.copyFrom(this.getParentModel().jacket);
			jacket.y -= 1f;
			matrixStack.scale(pixelScaling, heightScaling, pixelScaling);
			if(abstractClientPlayer.isCrouching()) {
				matrixStack.translate(0, 0, -0.025f);
			}
			jacket.render(matrixStack, vertices, light, overlay, 1.0f, color, color, 1.0f);
			matrixStack.popPose();
		}
		
	}

}
