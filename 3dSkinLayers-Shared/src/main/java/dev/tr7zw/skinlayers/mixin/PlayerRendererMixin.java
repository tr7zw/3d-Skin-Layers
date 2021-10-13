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
import dev.tr7zw.skinlayers.render.CustomizableModelPart;
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

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerRendererMixin(Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onCreate(CallbackInfo info) {
        this.addLayer(new HeadLayerFeatureRenderer(this));
        this.addLayer(new BodyLayerFeatureRenderer(this));
    }
    
    @Inject(method = "setModelProperties", at = @At("RETURN"))
    public void setModelProperties(AbstractClientPlayer abstractClientPlayer, CallbackInfo info) {
        if(Minecraft.getInstance().player.distanceToSqr(abstractClientPlayer) > SkinLayersModBase.config.renderDistanceLOD*SkinLayersModBase.config.renderDistanceLOD)return;
        PlayerModel<AbstractClientPlayer> playerModel = this.getModel();
        playerModel.hat.visible = !SkinLayersModBase.config.enableHat;
        playerModel.jacket.visible = !SkinLayersModBase.config.enableJacket;
        playerModel.leftSleeve.visible = !SkinLayersModBase.config.enableLeftSleeve;
        playerModel.rightSleeve.visible = !SkinLayersModBase.config.enableRightSleeve;
        playerModel.leftPants.visible = !SkinLayersModBase.config.enableLeftPants;
        playerModel.rightPants.visible = !SkinLayersModBase.config.enableRightPants;
    }
    
    @Inject(method = "renderHand", at = @At("RETURN"))
    private void renderHand(PoseStack poseStack, MultiBufferSource multiBufferSource, int i,
            AbstractClientPlayer abstractClientPlayer, ModelPart arm, ModelPart sleeve, CallbackInfo info) {
        if(sleeve.visible)return; // Vanilla one is active
        PlayerSettings settings = (PlayerSettings) abstractClientPlayer;
        float pixelScaling = 1.1f;
        float armHeightScaling = 1.1f;
        boolean thinArms = ((PlayerEntityModelAccessor)getModel()).hasThinArms();
        if(settings.getSkinLayers() == null && !SkinUtil.setup3dLayers(abstractClientPlayer, settings, thinArms, getModel())) {
            return;
        }
        CustomizableModelPart part = null;
        if(sleeve == this.model.leftSleeve) {
            part = settings.getSkinLayers()[2];
        }else {
            part = settings.getSkinLayers()[3];
        }
        part.copyFrom(arm);
        poseStack.pushPose();
        poseStack.scale(pixelScaling, armHeightScaling, pixelScaling);
        part.y -= 0.6;
        if(!thinArms) {
            part.x -= 0.4;
        }
        part.render(poseStack,
            multiBufferSource
                    .getBuffer(RenderType.entityTranslucent(abstractClientPlayer.getSkinTextureLocation())),
            i, OverlayTexture.NO_OVERLAY);
        part.setPos(0, 0, 0);
        part.setRotation(0, 0, 0);
        poseStack.popPose();

    }
    
}
