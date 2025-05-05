
package dev.tr7zw.skinlayers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if MC >= 12000 && FABRIC || NEOFORGE
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.skinlayers.accessor.ModelPartInjector;
import traben.entity_model_features.models.parts.EMFModelPart;

@Pseudo
@Mixin(EMFModelPart.class)
public abstract class EMFModelPartMixin implements ModelPartInjector {

    //#if MC >= 12100
    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V", at = @At(value = "HEAD"), cancellable = true)
    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, int color,
            CallbackInfo ci) {
        if (isVisible() && getInjectedMesh() != null) {
            poseStack.pushPose();
            prepareTranslateAndRotate(poseStack);
            getOffsetProvider().applyOffset(poseStack, getInjectedMesh());
            getInjectedMesh().render(null, poseStack, vertexConsumer, light, overlay, color);
            poseStack.popPose();
            ci.cancel();
        }
    }
    //#else
    //$$ @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V", at = @At(value = "HEAD"), cancellable = true)
    //$$ public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha,
    //$$         CallbackInfo ci) {
    //$$     if (isVisible() && getInjectedMesh() != null) {
    //$$         poseStack.pushPose();
    //$$         prepareTranslateAndRotate(poseStack);
    //$$         getOffsetProvider().applyOffset(poseStack, getInjectedMesh());
    //$$         getInjectedMesh().render(null, poseStack, vertexConsumer, light, overlay, red, green, blue, alpha);
    //$$         poseStack.popPose();
    //$$         ci.cancel();
    //$$     }
    //$$ }
    //$$ 
    //#endif

}
//#else
//$$ @Mixin(net.minecraft.client.Minecraft.class)
//$$ public abstract class EMFModelPartMixin{} // DUMMY
//#endif
