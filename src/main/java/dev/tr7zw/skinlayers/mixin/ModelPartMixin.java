package dev.tr7zw.skinlayers.mixin;

import java.util.Map;

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
    //? if >= 1.17.0 {

    @Shadow
    private Map<String, ModelPart> children;
    //? }

    @Getter
    private Mesh injectedMesh = null;
    @Getter
    private OffsetProvider offsetProvider = null;

    //? if >= 1.21.0 {

    @Inject(method = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V", at = @At(value = "HEAD"), cancellable = true)
    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, int color,
            CallbackInfo ci) {
        if (visible && injectedMesh != null) {
            poseStack.pushPose();
            translateAndRotate(poseStack);
            offsetProvider.applyOffset(poseStack, injectedMesh);
            injectedMesh.render((ModelPart) (Object) this, poseStack, vertexConsumer, light, overlay, color);
            poseStack.popPose();
            ci.cancel();
        }
    }
    //? } else {
    /*
     @Inject(method = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V", at = @At(value = "HEAD"), cancellable = true)
     public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha,
             CallbackInfo ci) {
         if (visible && injectedMesh != null) {
             poseStack.pushPose();
             translateAndRotate(poseStack);
             offsetProvider.applyOffset(poseStack, injectedMesh);
             injectedMesh.render((ModelPart)(Object)this, poseStack, vertexConsumer, light, overlay, red, green, blue, alpha);
             poseStack.popPose();
             ci.cancel();
             return;
         }
     //? if >= 1.17.0 {
    
             if(visible && dev.tr7zw.skinlayers.util.SodiumWorkaround.IS_SODIUM_WORKAROUND_NEEDED && (children.containsKey("head") || children.containsKey("hat"))) {
                     poseStack.pushPose();
                      translateAndRotate(poseStack);
                      compile(poseStack.last(), vertexConsumer, light, overlay, red, green, blue, alpha);
                      for(java.util.Map.Entry<String, ModelPart> child : this.children.entrySet()) {
                             child.getValue().render(poseStack, vertexConsumer, light, overlay, red, green, blue, alpha);
                      }       
                      poseStack.popPose();
                      ci.cancel();
             }
     //? }
     }
    
    *///? }

    @Override
    public void setInjectedMesh(Mesh mesh, OffsetProvider offsetProvider) {
        this.injectedMesh = mesh;
        this.offsetProvider = offsetProvider;
    }

    @Shadow
    public void translateAndRotate(PoseStack poseStack) {

    }

    //? if >= 1.17.0 && < 1.21.0 {
    /*
     @Shadow
     public void compile(PoseStack.Pose pose, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
     }
    *///? }

    @Override
    public void prepareTranslateAndRotate(PoseStack poseStack) {
        translateAndRotate(poseStack);
    }

}
