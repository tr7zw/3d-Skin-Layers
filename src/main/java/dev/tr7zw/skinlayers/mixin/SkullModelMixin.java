package dev.tr7zw.skinlayers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.skinlayers.accessor.ModelPartInjector;
import dev.tr7zw.skinlayers.accessor.SkullModelAccessor;
import dev.tr7zw.skinlayers.accessor.SkullModelStateAccessor;
import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.skinlayers.api.OffsetProvider;

import net.minecraft.client.model.geom.ModelPart;

//? if >= 1.21.11 {

import net.minecraft.client.model.object.skull.*;
//? } else {
/*
import net.minecraft.client.model.*;
*///? }

//? if >= 1.17.0 {

@Mixin(SkullModel.class)
//? } else {

// import net.minecraft.client.model.HumanoidHeadModel;
//
// @Mixin(HumanoidHeadModel.class)
//? }
public class SkullModelMixin implements SkullModelAccessor {

    @Shadow
    //? if >= 1.17.0 {

    private ModelPart head;
    //? } else {

    // private ModelPart hat;
    //? }

    @Override
    public void injectHatMesh(Mesh mesh) {
        //? if >= 1.17.0 {

        head.getAllParts().forEach(part -> {
            if (part != head && (Object) part instanceof ModelPartInjector inj) { // is the hat, not the head
                inj.setInjectedMesh(mesh, OffsetProvider.SKULL);
            }
        });
        //? } else {

        // ((ModelPartInjector)(Object)hat).setInjectedMesh(mesh, OffsetProvider.SKULL);
        //? }
    }

    //? if >= 1.21.9 {

    @Inject(method = "setupAnim", at = @At("HEAD"))
    public void setupAnim(SkullModelBase.State state, CallbackInfo ci) {
        if (state instanceof SkullModelStateAccessor accessor) {
            if (accessor.getSkullSettings() != null) {
                injectHatMesh(accessor.getSkullSettings().getMesh());
                return;
            }
        }
        // Otherwise clear it
        injectHatMesh(null);
    }
    //? }

}
