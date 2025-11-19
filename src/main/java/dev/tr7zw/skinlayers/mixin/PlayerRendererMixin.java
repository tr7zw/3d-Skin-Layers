package dev.tr7zw.skinlayers.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.SkinUtil;
import dev.tr7zw.skinlayers.accessor.ModelPartInjector;
import dev.tr7zw.skinlayers.accessor.PlayerEntityModelAccessor;
import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.api.OffsetProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
//? if < 1.21.9 {
/*
 import net.minecraft.client.renderer.MultiBufferSource;
 import net.minecraft.world.entity.EquipmentSlot;
 import net.minecraft.world.entity.player.PlayerModelPart;
 import net.minecraft.world.item.ItemStack;
*///? }
   //? if >= 1.21.9 {

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
//? } else if >= 1.21.2 {
/*
 import net.minecraft.client.renderer.entity.state.PlayerRenderState;
*///? } else {
/*
 import net.minecraft.client.renderer.RenderType;
 import net.minecraft.client.renderer.texture.OverlayTexture;
 import net.minecraft.world.entity.player.PlayerModelPart;
 import dev.tr7zw.skinlayers.api.Mesh;
*///? }
   //? if >= 1.17.0 {

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
//? } else {

// import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
//? }
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
//? if >= 1.21.9 {

import net.minecraft.client.renderer.entity.player.AvatarRenderer;
//? } else {
/*
 import net.minecraft.client.renderer.entity.player.PlayerRenderer;
*///? }
import net.minecraft.resources.ResourceLocation;

//? if >= 1.21.9 {

@Mixin(AvatarRenderer.class)
//? } else {
/*
 @Mixin(PlayerRenderer.class)
*///? }
public abstract class PlayerRendererMixin
        //? if >= 1.21.10 {

        extends LivingEntityRenderer<AbstractClientPlayer, AvatarRenderState, PlayerModel> {
    //? } else if >= 1.21.2 {
    /*
     extends LivingEntityRenderer<AbstractClientPlayer, PlayerRenderState, PlayerModel> {
    *///? } else {
    /*
            extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    *///? }

    private boolean setupFirstpersonArms = false;

    //? if >= 1.17.0 {

    public PlayerRendererMixin(Context context, PlayerModel entityModel, float f) {
        super(context, entityModel, f);
    }
    //? } else {

    //  public PlayerRendererMixin(EntityRenderDispatcher entityRenderDispatcher,
    // 		PlayerModel<AbstractClientPlayer> entityModel, float f) {
    // 	super(entityRenderDispatcher, entityModel, f);
    // }
    //? }

    // Moved to PlayerModelMixin
    //? if >= 1.21.2 {

    //? } else {
    /*
      private boolean loaded = false;
      
        @SuppressWarnings("resource")
        @Inject(method = "setModelProperties", at = @At("RETURN"))
        public void setModelProperties(AbstractClientPlayer abstractClientPlayer, CallbackInfo info) {
           PlayerModel playerModel = this.getModel();
           if (!loaded) {
               this.addLayer(new dev.tr7zw.skinlayers.renderlayers.CustomLayerFeatureRenderer(this));
    
              loaded = true;
          }
         PlayerSettings settings = (PlayerSettings) abstractClientPlayer;
          boolean slim = ((PlayerEntityModelAccessor) getModel()).hasThinArms();
         // reset all injected layers
          ((ModelPartInjector) (Object) playerModel.hat).setInjectedMesh(null, null);
         ((ModelPartInjector) (Object) playerModel.jacket).setInjectedMesh(null, null);
          ((ModelPartInjector) (Object) playerModel.leftSleeve).setInjectedMesh(null, null);
         ((ModelPartInjector) (Object) playerModel.rightSleeve).setInjectedMesh(null, null);
         ((ModelPartInjector) (Object) playerModel.leftPants).setInjectedMesh(null, null);
         ((ModelPartInjector) (Object) playerModel.rightPants).setInjectedMesh(null, null);
          if (Minecraft.getInstance().player == null
                  || abstractClientPlayer.distanceToSqr(Minecraft.getInstance().gameRenderer.getMainCamera()
                          .getPosition()) > SkinLayersModBase.config.renderDistanceLOD
                                 * SkinLayersModBase.config.renderDistanceLOD) {
              return;
          }
         if (!SkinUtil.setup3dLayers(abstractClientPlayer, settings, slim)) {
             // fall back to vanilla
             return;
         }
         if (SkinLayersModBase.config.compatibilityMode || setupFirstpersonArms) {
             setupFirstpersonArms = false;
             // Inject layers into the vanilla model
             ItemStack itemStack = abstractClientPlayer.getItemBySlot(EquipmentSlot.HEAD);
             if (SkinLayersModBase.config.enableHat && (itemStack == null
                     || !SkinLayersModBase.hideHeadLayers.contains(itemStack.getItem()))) {
                ((ModelPartInjector) (Object) playerModel.hat).setInjectedMesh(settings.getHeadMesh(),
                        OffsetProvider.HEAD);
            }
             if (SkinLayersModBase.config.enableJacket) {
                ((ModelPartInjector) (Object) playerModel.jacket).setInjectedMesh(settings.getTorsoMesh(),
                        OffsetProvider.BODY);
             }
             if (SkinLayersModBase.config.enableLeftSleeve) {
                 ((ModelPartInjector) (Object) playerModel.leftSleeve).setInjectedMesh(settings.getLeftArmMesh(),
                        slim ? OffsetProvider.LEFT_ARM_SLIM : OffsetProvider.LEFT_ARM);
            }
            if (SkinLayersModBase.config.enableRightSleeve) {
                ((ModelPartInjector) (Object) playerModel.rightSleeve).setInjectedMesh(settings.getRightArmMesh(),
                        slim ? OffsetProvider.RIGHT_ARM_SLIM : OffsetProvider.RIGHT_ARM);
            }
             if (SkinLayersModBase.config.enableLeftPants) {
                ((ModelPartInjector) (Object) playerModel.leftPants).setInjectedMesh(settings.getLeftLegMesh(),
                         OffsetProvider.LEFT_LEG);
            }
            if (SkinLayersModBase.config.enableRightPants) {
                ((ModelPartInjector) (Object) playerModel.rightPants).setInjectedMesh(settings.getRightLegMesh(),
                        OffsetProvider.RIGHT_LEG);
            }
        } else {
            // hiding vanilla layers when needed
            playerModel.hat.visible = playerModel.hat.visible && !SkinLayersModBase.config.enableHat;
             playerModel.jacket.visible = playerModel.jacket.visible && !SkinLayersModBase.config.enableJacket;
             playerModel.leftSleeve.visible = playerModel.leftSleeve.visible
                     && !SkinLayersModBase.config.enableLeftSleeve;
             playerModel.rightSleeve.visible = playerModel.rightSleeve.visible
                     && !SkinLayersModBase.config.enableRightSleeve;
             playerModel.leftPants.visible = playerModel.leftPants.visible && !SkinLayersModBase.config.enableLeftPants;
             playerModel.rightPants.visible = playerModel.rightPants.visible
                     && !SkinLayersModBase.config.enableRightPants;
         }
      }
    *///? }

    @Inject(method = "renderHand", at = @At("HEAD"))
    //? if >= 1.21.2 {

    private void renderHandStart(PoseStack poseStack,
            //? if >= 1.21.9 {

            net.minecraft.client.renderer.SubmitNodeCollector multiBufferSource,
            //? } else {
            /*
                     MultiBufferSource multiBufferSource,
                    *///? }
            int i, ResourceLocation resourceLocation, ModelPart arm, boolean bl, CallbackInfo info) {
        // TODO
        AbstractClientPlayer abstractClientPlayer = Minecraft.getInstance().player;// hacky, but 1.21.2 happened
        ModelPart sleeve;
        if (arm == getModel().leftArm) {
            sleeve = getModel().leftSleeve;
        } else {
            sleeve = getModel().rightSleeve;
        }
        //? } else {
        /*
            private void renderHandStart(PoseStack poseStack, MultiBufferSource multiBufferSource, int i,
                    AbstractClientPlayer abstractClientPlayer, ModelPart arm, ModelPart sleeve, CallbackInfo info) {
        *///? }
        PlayerSettings settings = (PlayerSettings) abstractClientPlayer;
        boolean slim = ((PlayerEntityModelAccessor) getModel()).hasThinArms();
        ((ModelPartInjector) (Object) sleeve).setInjectedMesh(null, null);
        if (!SkinUtil.setup3dLayers(abstractClientPlayer, settings, slim)) {
            // fall back to vanilla
            return;
        }
        setupFirstpersonArms = true;
        if (arm == getModel().leftArm) {
            if (SkinLayersModBase.config.enableLeftSleeve) {
                ((ModelPartInjector) (Object) sleeve).setInjectedMesh(settings.getLeftArmMesh(),
                        slim ? OffsetProvider.FIRSTPERSON_LEFT_ARM_SLIM : OffsetProvider.FIRSTPERSON_LEFT_ARM);
            }
        } else {
            if (SkinLayersModBase.config.enableRightSleeve) {
                ((ModelPartInjector) (Object) sleeve).setInjectedMesh(settings.getRightArmMesh(),
                        slim ? OffsetProvider.FIRSTPERSON_RIGHT_ARM_SLIM : OffsetProvider.FIRSTPERSON_RIGHT_ARM);
            }
        }
    }

    //? if >= 1.21.9 {

    @WrapOperation(method = "method_72996", at = @At(value = "NEW", target = "(Lnet/minecraft/client/model/geom/ModelPart;Z)Lnet/minecraft/client/model/PlayerModel;"))
    private static PlayerModel markArmorModelAsIgnored(ModelPart modelPart, boolean slim,
            Operation<PlayerModel> original) {
        PlayerModel call = original.call(modelPart, slim);
        ((PlayerEntityModelAccessor) call).setIgnored(true);
        return call;
    }
    //? }

}
