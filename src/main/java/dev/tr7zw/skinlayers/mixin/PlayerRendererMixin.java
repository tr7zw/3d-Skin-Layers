package dev.tr7zw.skinlayers.mixin;

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
import net.minecraft.client.renderer.MultiBufferSource;

// spotless:off 
//#if MC >= 12102
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
//#else
//$$import dev.tr7zw.util.NMSHelper;
//$$import net.minecraft.client.renderer.RenderType;
//$$import net.minecraft.client.renderer.texture.OverlayTexture;
//$$import net.minecraft.world.entity.player.PlayerModelPart;
//$$import dev.tr7zw.skinlayers.api.Mesh;
//#endif
//#if MC >= 11700
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
//#else
//$$ import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
//#endif
// spotless:on
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin
// spotless:off 
//#if MC >= 12102
            extends LivingEntityRenderer<AbstractClientPlayer, PlayerRenderState, PlayerModel> {
//#else
//$$        extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
//#endif
// spotless:on

    private boolean setupFirstpersonArms = false;

    // spotless:off 
	//#if MC >= 11700
    public PlayerRendererMixin(Context context, PlayerModel entityModel, float f) {
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

    // spotless:off 
  //#if MC >= 12102
  @Inject(method = "extractRenderState", at = @At("RETURN"))
  public void extractRenderState(AbstractClientPlayer abstractClientPlayer, PlayerRenderState playerRenderState,
          float f, CallbackInfo ci) {
      PlayerModel playerModel = this.getModel();
      if (Minecraft.getInstance().player == null
              || abstractClientPlayer.distanceToSqr(Minecraft.getInstance().gameRenderer.getMainCamera()
                      .getPosition()) > SkinLayersModBase.config.renderDistanceLOD
                              * SkinLayersModBase.config.renderDistanceLOD) {
          return;
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
      if (!SkinUtil.setup3dLayers(abstractClientPlayer, settings, slim)) {
          // fall back to vanilla
          return;
      }
      // Inject layers into the vanilla model
      ItemStack itemStack = abstractClientPlayer.getItemBySlot(EquipmentSlot.HEAD);
      if (SkinLayersModBase.config.enableHat
              && (itemStack == null || !SkinLayersModBase.hideHeadLayers.contains(itemStack.getItem()))) {
          ((ModelPartInjector) (Object) playerModel.hat).setInjectedMesh(settings.getHeadMesh(), OffsetProvider.HEAD);
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
  }
//#else
  //$$  private boolean loaded = false;
  //$$  
  //$$    @SuppressWarnings("resource")
  //$$    @Inject(method = "setModelProperties", at = @At("RETURN"))
  //$$    public void setModelProperties(AbstractClientPlayer abstractClientPlayer, CallbackInfo info) {
  //$$       PlayerModel playerModel = this.getModel();
  //$$       if (!loaded) {
   //$$           this.addLayer(new dev.tr7zw.skinlayers.renderlayers.CustomLayerFeatureRenderer(this));
  //$$
  //$$          loaded = true;
  //$$      }
  //$$      if (Minecraft.getInstance().player == null
  //$$              || abstractClientPlayer.distanceToSqr(Minecraft.getInstance().gameRenderer.getMainCamera()
  //$$                      .getPosition()) > SkinLayersModBase.config.renderDistanceLOD
  //$$                             * SkinLayersModBase.config.renderDistanceLOD) {
  //$$          return;
  //$$      }
  //$$     PlayerSettings settings = (PlayerSettings) abstractClientPlayer;
  //$$      boolean slim = ((PlayerEntityModelAccessor) getModel()).hasThinArms();
  //$$     // reset all injected layers
  //$$      ((ModelPartInjector) (Object) playerModel.hat).setInjectedMesh(null, null);
  //$$     ((ModelPartInjector) (Object) playerModel.jacket).setInjectedMesh(null, null);
  //$$      ((ModelPartInjector) (Object) playerModel.leftSleeve).setInjectedMesh(null, null);
  //$$     ((ModelPartInjector) (Object) playerModel.rightSleeve).setInjectedMesh(null, null);
  //$$     ((ModelPartInjector) (Object) playerModel.leftPants).setInjectedMesh(null, null);
  //$$     ((ModelPartInjector) (Object) playerModel.rightPants).setInjectedMesh(null, null);
  //$$     if (!SkinUtil.setup3dLayers(abstractClientPlayer, settings, slim)) {
  //$$         // fall back to vanilla
  //$$         return;
  //$$     }
  //$$     if (SkinLayersModBase.config.compatibilityMode || setupFirstpersonArms) {
  //$$         setupFirstpersonArms = false;
  //$$         // Inject layers into the vanilla model
  //$$         ItemStack itemStack = abstractClientPlayer.getItemBySlot(EquipmentSlot.HEAD);
  //$$         if (SkinLayersModBase.config.enableHat && (itemStack == null
  //$$                 || !SkinLayersModBase.hideHeadLayers.contains(itemStack.getItem()))) {
  //$$            ((ModelPartInjector) (Object) playerModel.hat).setInjectedMesh(settings.getHeadMesh(),
  //$$                    OffsetProvider.HEAD);
  //$$        }
  //$$         if (SkinLayersModBase.config.enableJacket) {
  //$$            ((ModelPartInjector) (Object) playerModel.jacket).setInjectedMesh(settings.getTorsoMesh(),
  //$$                    OffsetProvider.BODY);
  //$$         }
  //$$         if (SkinLayersModBase.config.enableLeftSleeve) {
  //$$             ((ModelPartInjector) (Object) playerModel.leftSleeve).setInjectedMesh(settings.getLeftArmMesh(),
  //$$                    slim ? OffsetProvider.LEFT_ARM_SLIM : OffsetProvider.LEFT_ARM);
  //$$        }
  //$$        if (SkinLayersModBase.config.enableRightSleeve) {
  //$$            ((ModelPartInjector) (Object) playerModel.rightSleeve).setInjectedMesh(settings.getRightArmMesh(),
  //$$                    slim ? OffsetProvider.RIGHT_ARM_SLIM : OffsetProvider.RIGHT_ARM);
  //$$        }
  //$$         if (SkinLayersModBase.config.enableLeftPants) {
  //$$            ((ModelPartInjector) (Object) playerModel.leftPants).setInjectedMesh(settings.getLeftLegMesh(),
  //$$                     OffsetProvider.LEFT_LEG);
  //$$        }
  //$$        if (SkinLayersModBase.config.enableRightPants) {
  //$$            ((ModelPartInjector) (Object) playerModel.rightPants).setInjectedMesh(settings.getRightLegMesh(),
  //$$                    OffsetProvider.RIGHT_LEG);
  //$$        }
  //$$    } else {
  //$$        // hiding vanilla layers when needed
  //$$        playerModel.hat.visible = playerModel.hat.visible && !SkinLayersModBase.config.enableHat;
  //$$         playerModel.jacket.visible = playerModel.jacket.visible && !SkinLayersModBase.config.enableJacket;
  //$$         playerModel.leftSleeve.visible = playerModel.leftSleeve.visible
  //$$                 && !SkinLayersModBase.config.enableLeftSleeve;
  //$$         playerModel.rightSleeve.visible = playerModel.rightSleeve.visible
  //$$                 && !SkinLayersModBase.config.enableRightSleeve;
  //$$         playerModel.leftPants.visible = playerModel.leftPants.visible && !SkinLayersModBase.config.enableLeftPants;
  //$$         playerModel.rightPants.visible = playerModel.rightPants.visible
  //$$                 && !SkinLayersModBase.config.enableRightPants;
  //$$     }
  //$$  }
  //#endif
  // spotless:on

    @Inject(method = "renderHand", at = @At("HEAD"))
// spotless:off 
//#if MC >= 12102
  private void renderHandStart(PoseStack poseStack, MultiBufferSource multiBufferSource, int i,
          ResourceLocation resourceLocation, ModelPart arm, boolean bl, CallbackInfo info) {
        // TODO
        AbstractClientPlayer abstractClientPlayer = Minecraft.getInstance().player;// hacky, but 1.21.2 happened
        ModelPart sleeve;
        if (arm == getModel().leftArm) {
            sleeve = getModel().leftSleeve;
        } else {
            sleeve = getModel().rightSleeve;
        }
//#else
//$$    private void renderHandStart(PoseStack poseStack, MultiBufferSource multiBufferSource, int i,
//$$            AbstractClientPlayer abstractClientPlayer, ModelPart arm, ModelPart sleeve, CallbackInfo info) {
//#endif
// spotless:on
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
                        slim ? OffsetProvider.LEFT_ARM_SLIM : OffsetProvider.LEFT_ARM);
            }
        } else {
            if (SkinLayersModBase.config.enableRightSleeve) {
                ((ModelPartInjector) (Object) sleeve).setInjectedMesh(settings.getRightArmMesh(),
                        slim ? OffsetProvider.RIGHT_ARM_SLIM : OffsetProvider.RIGHT_ARM);
            }
        }
    }

}
