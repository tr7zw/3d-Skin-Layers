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
import dev.tr7zw.skinlayers.renderlayers.CustomLayerFeatureRenderer;
import dev.tr7zw.util.NMSHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.PlayerModelPart;
// spotless:off 
//#if MC >= 11700
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
//#else
//$$ import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
//#endif
// spotless:on

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin
        extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    // spotless:off 
	//#if MC >= 11700
    public PlayerRendererMixin(Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) {
        super(context, entityModel, f);
    }
    //#else
    //$$  public PlayerRendererMixin(EntityRenderDispatcher entityRenderDispatcher,
    //$$ 		PlayerModel<AbstractClientPlayer> entityModel, float f) {
    //$$ 	super(entityRenderDispatcher, entityModel, f);
    //$$ 	// TODO Auto-generated constructor stub
    //$$ }
    //#endif
    // spotless:on

    private boolean loaded = false;

// Somehow doing this in 1.16.5 is a bit unpredictable, only late adding layer works well. Not sure why
//    @Inject(method = "<init>*", at = @At("RETURN"))
//    public void onCreate(CallbackInfo info) {
//        this.addLayer(new CustomLayerFeatureRenderer(this));
//    }

    @SuppressWarnings("resource")
    @Inject(method = "setModelProperties", at = @At("RETURN"))
    public void setModelProperties(AbstractClientPlayer abstractClientPlayer, CallbackInfo info) {
        if (!loaded) {
            this.addLayer(new CustomLayerFeatureRenderer(this));
            loaded = true;
        }
        if (Minecraft.getInstance().player == null
                || abstractClientPlayer.distanceToSqr(Minecraft.getInstance().gameRenderer.getMainCamera()
                        .getPosition()) > SkinLayersModBase.config.renderDistanceLOD
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
                        .getBuffer(RenderType.entityTranslucent(NMSHelper.getPlayerSkin(abstractClientPlayer))),
                i, OverlayTexture.NO_OVERLAY);
        part.setPosition(0, 0, 0);
        part.setRotation(0, 0, 0);
        poseStack.popPose();

    }

}
