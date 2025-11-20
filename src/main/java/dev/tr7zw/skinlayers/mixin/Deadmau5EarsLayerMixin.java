package dev.tr7zw.skinlayers.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.skinlayers.accessor.PlayerEntityModelAccessor;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.*;

//? if >= 1.21.11 {

import net.minecraft.client.model.player.*;
//? }

@Mixin(Deadmau5EarsLayer.class)
public class Deadmau5EarsLayerMixin {

    //? if >= 1.21.9 {

    @Shadow
    @Final
    private HumanoidModel<net.minecraft.client.renderer.entity.state.AvatarRenderState> model;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(
            RenderLayerParent<net.minecraft.client.renderer.entity.state.AvatarRenderState, PlayerModel> renderer,
            EntityModelSet modelSet, CallbackInfo ci) {
        ((PlayerEntityModelAccessor) model).setIgnored(true);
    }
    //? }

}
