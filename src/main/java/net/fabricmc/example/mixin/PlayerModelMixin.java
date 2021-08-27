package net.fabricmc.example.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.example.accessor.PlayerEntityModelAccessor;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

@Mixin(PlayerModel.class)
public class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> implements PlayerEntityModelAccessor {

    public PlayerModelMixin(ModelPart modelPart) {
        super(modelPart);
    }

    @Shadow
    private boolean slim;

    @Override
    public boolean hasThinArms() {
        return slim;
    }
    
}
