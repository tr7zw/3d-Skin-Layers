package dev.tr7zw.skinlayers.renderlayers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.SkinUtil;
import dev.tr7zw.skinlayers.accessor.PlayerEntityModelAccessor;
import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.api.LayerFeatureTransformerAPI;
import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.skinlayers.render.SolidPixelWrapper.Dimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.player.PlayerModelPart;

public class BodyLayerFeatureRenderer 
extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
	public BodyLayerFeatureRenderer(
	        RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
			super(renderLayerParent);
			thinArms = ((PlayerEntityModelAccessor)getParentModel()).hasThinArms();
			bodyLayers.add(new Layer(PlayerSettings::getLeftLegMesh, false, PlayerModelPart.LEFT_PANTS_LEG, Shape.LEGS, () -> this.getParentModel().leftLeg, () -> SkinLayersModBase.config.enableLeftPants));
			bodyLayers.add(new Layer(PlayerSettings::getRightLegMesh, false, PlayerModelPart.RIGHT_PANTS_LEG, Shape.LEGS, () -> this.getParentModel().rightLeg, () -> SkinLayersModBase.config.enableRightPants));
			bodyLayers.add(new Layer(PlayerSettings::getLeftArmMesh, false, PlayerModelPart.LEFT_SLEEVE, thinArms ? Shape.ARMS_SLIM : Shape.ARMS, () -> this.getParentModel().leftArm, () -> SkinLayersModBase.config.enableLeftSleeve));
			bodyLayers.add(new Layer(PlayerSettings::getRightArmMesh, true, PlayerModelPart.RIGHT_SLEEVE, thinArms ? Shape.ARMS_SLIM : Shape.ARMS, () -> this.getParentModel().rightArm, () -> SkinLayersModBase.config.enableRightSleeve));
			bodyLayers.add(new Layer(PlayerSettings::getTorsoMesh, false, PlayerModelPart.JACKET, Shape.BODY, () -> this.getParentModel().body, () -> SkinLayersModBase.config.enableJacket));
	}

	private final boolean thinArms;

	private static final Minecraft mc = Minecraft.getInstance();
	
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i,
            AbstractClientPlayer player, float f, float g, float h, float j, float k,
            float l) {
        if (!player.isSkinLoaded() || player.isInvisible()) {
            return;
        }
        if(mc.level == null) {
            return; // in a menu or something and the model gets rendered
        }
        if(mc.player.distanceToSqr(player) > SkinLayersModBase.config.renderDistanceLOD*SkinLayersModBase.config.renderDistanceLOD)return;

		PlayerSettings settings = (PlayerSettings) player;
		// check for it being setup first to speedup the rendering
		if(!SkinUtil.setup3dLayers(player, settings, thinArms, this.getParentModel())) {
			return; // no head layer setup and wasn't able to setup
		}

		VertexConsumer vertexConsumer = multiBufferSource
				.getBuffer(RenderType.entityTranslucentCull(player.getSkinTextureLocation()));
		int m = LivingEntityRenderer.getOverlayCoords(player, 0.0f);
		renderLayers(player, settings, poseStack, vertexConsumer, i, m);
	}

    private final List<Layer> bodyLayers = new ArrayList<>();
    
    private record Layer(Function<PlayerSettings, Mesh> meshGetter, boolean mirrored, PlayerModelPart modelPart, Shape shape, Supplier<ModelPart> vanillaGetter, Supplier<Boolean> configGetter) {
    }
    
    private enum Shape {
        HEAD(0, new Dimensions(8,8,8)), BODY(-0.2f, new Dimensions(8,12,4)), LEGS(-0.2f, new Dimensions(4,14,4)), ARMS(-0.1f, new Dimensions(4,14,4)), ARMS_SLIM(-0.1f, new Dimensions(3,14,4))
        ;
        
        private final float yOffsetMagicValue;
        @SuppressWarnings("unused")
        private final Dimensions dimensions; // unused, maybe useful to correct some deformations

        private Shape(float yOffsetMagicValue, Dimensions dimensions) {
            this.dimensions = dimensions;
            this.yOffsetMagicValue = yOffsetMagicValue;
        }

    }
    
	public void renderLayers(AbstractClientPlayer abstractClientPlayer, PlayerSettings settings, PoseStack matrixStack, VertexConsumer vertices, int light, int overlay) {
		float pixelScaling = SkinLayersModBase.config.baseVoxelSize;
		float heightScaling = 1.035f;
		float widthScaling = SkinLayersModBase.config.baseVoxelSize;
		// Overlay refuses to work correctly, this is a workaround for now
	    boolean red = abstractClientPlayer.hurtTime > 0 || abstractClientPlayer.deathTime > 0;
	    float color = red ? 0.5f : 1f;
		for(Layer layer : bodyLayers) {
		    Mesh mesh = layer.meshGetter.apply(settings);
		    if(mesh != null && abstractClientPlayer.isModelPartShown(layer.modelPart) && layer.vanillaGetter.get().visible && layer.configGetter.get()) {
	            matrixStack.pushPose();
				LayerFeatureTransformerAPI.getTransformer().transform(abstractClientPlayer, matrixStack, layer.vanillaGetter.get());
	            layer.vanillaGetter.get().translateAndRotate(matrixStack);
	            float x = 0;
	            float y = 0;
	            if(layer.shape == Shape.ARMS) {
	                x = 0.998f;
	            } else if(layer.shape == Shape.ARMS_SLIM) {
	                x = 0.499f;
	            }
	            if(layer.shape == Shape.BODY) {
	                widthScaling = SkinLayersModBase.config.bodyVoxelWidthSize;
	            }
	            if(layer.mirrored) {
	                x *= -1;
	            }
	            matrixStack.scale(widthScaling, heightScaling, pixelScaling);
	            y = layer.shape.yOffsetMagicValue;
	            
	            mesh.setPosition(x, y, 0);
	            
	            mesh.render(layer.vanillaGetter.get(), matrixStack, vertices, light, overlay, 1.0f, color, color, 1.0f);
	            matrixStack.popPose();
	        }
		}
		
	}
    
}
