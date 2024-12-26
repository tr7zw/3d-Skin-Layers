package dev.tr7zw.skinlayers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.skinlayers.accessor.ModelPartInjector;
import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.skinlayers.api.OffsetProvider;
import lombok.Getter;
import net.minecraft.client.model.geom.ModelPart;

@Mixin(value = ModelPart.class, priority = 300)
public class ModelPartMixin implements ModelPartInjector {

    @Shadow
    @Getter
    boolean visible;

    @Getter
    private Mesh injectedMesh = null;
    @Getter
    private OffsetProvider offsetProvider = null;

    //#if MC >= 12100
    @Inject(method = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V", at = @At(value = "HEAD"), cancellable = true)
    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, int color,
            CallbackInfo ci) {
        if (visible && injectedMesh != null) {
            poseStack.pushPose();
            translateAndRotate(poseStack);
            offsetProvider.applyOffset(poseStack, injectedMesh);
            injectedMesh.render(null, poseStack, vertexConsumer, light, overlay, color);
            poseStack.popPose();
            ci.cancel();
        }
    }
    //#else
    //$$ @Inject(method = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V", at = @At(value = "HEAD"), cancellable = true)
    //$$ public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha,
    //$$         CallbackInfo ci) {
    //$$     if (visible && injectedMesh != null) {
    //$$         poseStack.pushPose();
    //$$         translateAndRotate(poseStack);
    //$$         offsetProvider.applyOffset(poseStack, injectedMesh);
    //$$         injectedMesh.render(null, poseStack, vertexConsumer, light, overlay, red, green, blue, alpha);
    //$$         poseStack.popPose();
    //$$         ci.cancel();
    //$$     }
    //$$ }
    //$$ 
    //#endif

    @Override
    public void setInjectedMesh(Mesh mesh, OffsetProvider offsetProvider) {
        this.injectedMesh = mesh;
        this.offsetProvider = offsetProvider;
    }

    @Shadow
    public void translateAndRotate(PoseStack poseStack) {

    }

    @Override
    public void prepareTranslateAndRotate(PoseStack poseStack) {
        translateAndRotate(poseStack);
    }

}
