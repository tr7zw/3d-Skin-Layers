package dev.tr7zw.skinlayers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.renderlayers.HeadLayerFeatureRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;

@Mixin(RenderPlayer.class)
public abstract class PlayerRendererMixin extends RendererLivingEntity<AbstractClientPlayer> {


    public PlayerRendererMixin(RenderManager p_i46156_1_, ModelBase p_i46156_2_, float p_i46156_3_) {
        super(p_i46156_1_, p_i46156_2_, p_i46156_3_);
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onCreate(CallbackInfo info) {
        this.addLayer(new HeadLayerFeatureRenderer((RenderPlayer)(Object)this));
        //this.addLayer(new BodyLayerFeatureRenderer(this));
    }
    
    @Inject(method = "setModelVisibilities", at = @At("RETURN"))
    public void setModelProperties(AbstractClientPlayer abstractClientPlayer, CallbackInfo info) {
        if(Minecraft.getMinecraft().thePlayer.getPositionVector().squareDistanceTo(abstractClientPlayer.getPositionVector()) > SkinLayersModBase.config.renderDistanceLOD*SkinLayersModBase.config.renderDistanceLOD)return;
        ModelPlayer playerModel = ((RenderPlayer)(Object)this).getMainModel();
        playerModel.bipedHeadwear.isHidden = !SkinLayersModBase.config.enableHat;
        playerModel.bipedBodyWear.isHidden = !SkinLayersModBase.config.enableJacket;
        playerModel.bipedLeftArmwear.isHidden = !SkinLayersModBase.config.enableLeftSleeve;
        playerModel.bipedRightArmwear.isHidden = !SkinLayersModBase.config.enableRightSleeve;
        playerModel.bipedLeftLegwear.isHidden = !SkinLayersModBase.config.enableLeftPants;
        playerModel.bipedRightLegwear.isHidden = !SkinLayersModBase.config.enableRightPants;
    }
    
//    @Inject(method = "renderHand", at = @At("RETURN"))
//    private void renderHand(PoseStack poseStack, MultiBufferSource multiBufferSource, int i,
//            AbstractClientPlayer abstractClientPlayer, ModelPart arm, ModelPart sleeve, CallbackInfo info) {
//        if(sleeve.visible)return; // Vanilla one is active
//        PlayerSettings settings = (PlayerSettings) abstractClientPlayer;
//        float pixelScaling = 1.1f;
//        float armHeightScaling = 1.1f;
//        boolean thinArms = ((PlayerEntityModelAccessor)getModel()).hasThinArms();
//        if(settings.getSkinLayers() == null && !SkinUtil.setup3dLayers(abstractClientPlayer, settings, thinArms, getModel())) {
//            return;
//        }
//        CustomizableModelPart part = null;
//        if(sleeve == this.model.leftSleeve) {
//            part = settings.getSkinLayers()[2];
//        }else {
//            part = settings.getSkinLayers()[3];
//        }
//        part.copyFrom(arm);
//        poseStack.pushPose();
//        poseStack.scale(pixelScaling, armHeightScaling, pixelScaling);
//        part.y -= 0.6;
//        if(!thinArms) {
//            part.x -= 0.4;
//        }
//        part.render(poseStack,
//            multiBufferSource
//                    .getBuffer(RenderType.entityTranslucent(abstractClientPlayer.getSkinTextureLocation())),
//            i, OverlayTexture.NO_OVERLAY);
//        part.setPos(0, 0, 0);
//        part.setRotation(0, 0, 0);
//        poseStack.popPose();
//
//    }
    
}
