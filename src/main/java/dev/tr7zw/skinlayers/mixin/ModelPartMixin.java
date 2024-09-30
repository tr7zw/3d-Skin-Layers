package dev.tr7zw.skinlayers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.skinlayers.accessor.ModelPartInjector;
import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.skinlayers.api.OffsetProvider;
import net.minecraft.client.model.geom.ModelPart;

@Mixin(ModelPart.class)
public class ModelPartMixin implements ModelPartInjector {

    private Mesh injectedMesh = null;
    private OffsetProvider offsetProvider = null;

    @Inject(method = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, int color,
            CallbackInfo ci) {
        if (injectedMesh != null) {
            offsetProvider.applyOffset(poseStack, injectedMesh);
            injectedMesh.render(null, poseStack, vertexConsumer, light, overlay, color);
        }
    }

    @Override
    public void setInjectedMesh(Mesh mesh, OffsetProvider offsetProvider) {
        injectedMesh = mesh;
        this.offsetProvider = offsetProvider;
    }

}
