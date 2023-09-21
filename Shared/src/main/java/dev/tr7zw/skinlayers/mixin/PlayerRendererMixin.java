package dev.tr7zw.skinlayers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.SkinUtil;
import dev.tr7zw.skinlayers.accessor.PlayerEntityModelAccessor;
import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.skinlayers.renderlayers.BodyLayerFeatureRenderer;
import dev.tr7zw.skinlayers.renderlayers.HeadLayerFeatureRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.PlayerModelPart;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin
        extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerRendererMixin(Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onCreate(CallbackInfo info) {
        this.addLayer(new HeadLayerFeatureRenderer(this));
        this.addLayer(new BodyLayerFeatureRenderer(this));
    }

    @SuppressWarnings("resource")
    @Inject(method = "setModelProperties", at = @At("RETURN"))
    public void setModelProperties(AbstractClientPlayer abstractClientPlayer, CallbackInfo info) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().player
                .distanceToSqr(abstractClientPlayer) > SkinLayersModBase.config.renderDistanceLOD
                        * SkinLayersModBase.config.renderDistanceLOD) {
            return;
        }
        PlayerModel<AbstractClientPlayer> playerModel = this.getModel();
        PlayerSettings settings = (PlayerSettings) abstractClientPlayer;
        if (settings.getHeadMesh() == null) {
            return; // fall back to vanilla
        }
        playerModel.hat.visible = playerModel.hat.visible && !SkinLayersModBase.config.enableHat;
        playerModel.jacket.visible = playerModel.jacket.visible && !SkinLayersModBase.config.enableJacket;
        playerModel.leftSleeve.visible = playerModel.leftSleeve.visible && !SkinLayersModBase.config.enableLeftSleeve;
        playerModel.rightSleeve.visible = playerModel.rightSleeve.visible
                && !SkinLayersModBase.config.enableRightSleeve;
        playerModel.leftPants.visible = playerModel.leftPants.visible && !SkinLayersModBase.config.enableLeftPants;
        playerModel.rightPants.visible = playerModel.rightPants.visible && !SkinLayersModBase.config.enableRightPants;
    }

    @Inject(method = "renderHand", at = @At("RETURN"))
    private void renderHand(PoseStack poseStack, MultiBufferSource multiBufferSource, int i,
            AbstractClientPlayer abstractClientPlayer, ModelPart arm, ModelPart sleeve, CallbackInfo info) {
        boolean rightSleeve = this.getModel().leftSleeve == sleeve ? false : true;

        if (rightSleeve ? !SkinLayersModBase.config.enableRightSleeve : !SkinLayersModBase.config.enableLeftSleeve)
            return; // Vanilla is active
        sleeve.visible = false; // hide the vanilla sleeve
        // Check the vanilla hide setting
        if (!abstractClientPlayer
                .isModelPartShown(rightSleeve ? PlayerModelPart.RIGHT_SLEEVE : PlayerModelPart.LEFT_SLEEVE))
            return;
        PlayerSettings settings = (PlayerSettings) abstractClientPlayer;

        float armHeightScaling = 1.1f;
        boolean thinArms = ((PlayerEntityModelAccessor) getModel()).hasThinArms();
        if (!SkinUtil.setup3dLayers(abstractClientPlayer, settings, thinArms, getModel())) {
            return;
        }
        Mesh part = sleeve == this.model.leftSleeve ? settings.getLeftArmMesh() : settings.getRightArmMesh();
        part.copyFrom(arm);
        poseStack.pushPose();
        poseStack.scale(SkinLayersModBase.config.firstPersonPixelScaling, armHeightScaling,
                SkinLayersModBase.config.firstPersonPixelScaling);
        boolean left = sleeve == this.model.leftSleeve;
        float x = left ? 5f : -5f;
        float y = 1.4f;
        double scaleOffset = (SkinLayersModBase.config.firstPersonPixelScaling - 1.1) * 5;
        if (left) {
            x -= scaleOffset;
        } else {
            x += scaleOffset;
        }
        if (!thinArms) {
            if (left) {
                x += 0.45;
            } else {
                x -= 0.45;
            }
        }
        part.setPosition(x, y, 0);
        part.render(poseStack,
                multiBufferSource
                        .getBuffer(RenderType.entityTranslucent(abstractClientPlayer.getSkin().texture())),
                i, OverlayTexture.NO_OVERLAY);
        part.setPosition(0, 0, 0);
        part.setRotation(0, 0, 0);
        poseStack.popPose();

    }

}
