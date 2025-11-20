package dev.tr7zw.skinlayers.mixin;

import lombok.Setter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.SkinUtil;
import dev.tr7zw.skinlayers.accessor.ModelPartInjector;
import dev.tr7zw.skinlayers.accessor.PlayerEntityModelAccessor;
import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.api.OffsetProvider;
import dev.tr7zw.transition.mc.entitywrapper.EntityRenderStateExtender;
import net.minecraft.client.Minecraft;
//? if >= 1.21.11 {

import net.minecraft.client.model.player.*;
//? }
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Mixin(PlayerModel.class)
public class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel implements PlayerEntityModelAccessor {

    //? if >= 1.17.0 {

    public PlayerModelMixin(ModelPart modelPart) {
        super(modelPart);
    }
    //? } else {

    // public PlayerModelMixin(float f) {
    // super(f);
    // }
    //? }

    @Shadow
    public ModelPart leftSleeve;
    @Shadow
    public ModelPart rightSleeve;
    @Shadow
    public ModelPart leftPants;
    @Shadow
    public ModelPart rightPants;
    @Shadow
    public ModelPart jacket;
    @Shadow
    private boolean slim;

    @Setter
    private boolean ignored;

    @Override
    public boolean hasThinArms() {
        return slim;
    }

    //? if >= 1.21.2 {

    @Inject(method = "setupAnim", at = @At("TAIL"), cancellable = true)
    //? if >= 1.21.9 {

    public void setupAnim(net.minecraft.client.renderer.entity.state.AvatarRenderState playerRenderState,
            CallbackInfo ci) {
        net.minecraft.world.entity.Avatar abstractClientPlayer = null;
        if (ignored)
            return;
        if (!((Object) this instanceof PlayerCapeModel)
                && playerRenderState instanceof EntityRenderStateExtender extender
                && extender.getTransitionEntity() instanceof net.minecraft.world.entity.Avatar entity) {
            abstractClientPlayer = entity;
        } else {
            return;
        }
        //? } else {
        /*
         public void setupAnim(
         net.minecraft.client.renderer.entity.state.PlayerRenderState playerRenderState,
         CallbackInfo ci) {
         net.minecraft.client.player.AbstractClientPlayer abstractClientPlayer = null;
         if (playerRenderState instanceof EntityRenderStateExtender extender && extender.getTransitionEntity() instanceof net.minecraft.client.player.AbstractClientPlayer entity) {
            abstractClientPlayer = entity;
         } else {
             return;
         }
        *///? }

        PlayerSettings settings = (PlayerSettings) abstractClientPlayer;
        // reset all injected layers
        ((ModelPartInjector) (Object) hat).setInjectedMesh(null, null);
        ((ModelPartInjector) (Object) jacket).setInjectedMesh(null, null);
        ((ModelPartInjector) (Object) leftSleeve).setInjectedMesh(null, null);
        ((ModelPartInjector) (Object) rightSleeve).setInjectedMesh(null, null);
        ((ModelPartInjector) (Object) leftPants).setInjectedMesh(null, null);
        ((ModelPartInjector) (Object) rightPants).setInjectedMesh(null, null);
        if (Minecraft.getInstance().player == null || abstractClientPlayer.distanceToSqr(Minecraft
                .getInstance().gameRenderer.getMainCamera()
                /*? >= 1.21.11 {*/ .position() /*?} else {*//* .getPosition() *//*?}*/) > SkinLayersModBase.config.renderDistanceLOD
                        * SkinLayersModBase.config.renderDistanceLOD) {
            return;
        }
        if (!SkinUtil.setup3dLayers(abstractClientPlayer, settings, slim)) {
            // fall back to vanilla
            return;
        }
        // Inject layers into the vanilla model
        ItemStack itemStack = abstractClientPlayer.getItemBySlot(EquipmentSlot.HEAD);
        if (SkinLayersModBase.config.enableHat
                && (itemStack == null || !SkinLayersModBase.hideHeadLayers.contains(itemStack.getItem()))) {
            ((ModelPartInjector) (Object) hat).setInjectedMesh(settings.getHeadMesh(), OffsetProvider.HEAD);
        }
        if (SkinLayersModBase.config.enableJacket) {
            ((ModelPartInjector) (Object) jacket).setInjectedMesh(settings.getTorsoMesh(), OffsetProvider.BODY);
        }
        if (SkinLayersModBase.config.enableLeftSleeve) {
            ((ModelPartInjector) (Object) leftSleeve).setInjectedMesh(settings.getLeftArmMesh(),
                    slim ? OffsetProvider.LEFT_ARM_SLIM : OffsetProvider.LEFT_ARM);
        }
        if (SkinLayersModBase.config.enableRightSleeve) {
            ((ModelPartInjector) (Object) rightSleeve).setInjectedMesh(settings.getRightArmMesh(),
                    slim ? OffsetProvider.RIGHT_ARM_SLIM : OffsetProvider.RIGHT_ARM);
        }
        if (SkinLayersModBase.config.enableLeftPants) {
            ((ModelPartInjector) (Object) leftPants).setInjectedMesh(settings.getLeftLegMesh(),
                    OffsetProvider.LEFT_LEG);
        }
        if (SkinLayersModBase.config.enableRightPants) {
            ((ModelPartInjector) (Object) rightPants).setInjectedMesh(settings.getRightLegMesh(),
                    OffsetProvider.RIGHT_LEG);
        }
    }
    //? }

}
