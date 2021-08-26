package net.fabricmc.example.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> {

    public PlayerModelMixin(ModelPart modelPart) {
        super(modelPart);
    }

    private ModelPart hat3d;
    
    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onCreate(ModelPart modelPart, boolean bl, CallbackInfo info) {
        this.hat3d = modelPart.getChild("hat3d");
    }
    
    @Inject(method = "createMesh", at = @At("RETURN"))
    public static void createMesh(CubeDeformation cubeDeformation, boolean bl, CallbackInfoReturnable<MeshDefinition> info) {
        MeshDefinition meshDefinition = info.getReturnValue();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("hat3d",
                CubeListBuilder.create().texOffs(24, 0).addBox(-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 1.0F, cubeDeformation),
                PartPose.ZERO);
    }
    
}
