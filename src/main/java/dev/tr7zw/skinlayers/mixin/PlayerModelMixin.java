package dev.tr7zw.skinlayers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.tr7zw.skinlayers.accessor.PlayerEntityModelAccessor;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

@Mixin(PlayerModel.class)
public class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel implements PlayerEntityModelAccessor {

	//#if MC >= 11700
    public PlayerModelMixin(ModelPart modelPart) {
        super(modelPart);
    }
    //#else
    //$$ public PlayerModelMixin(float f) {
	//$$	super(f);
	//$$ }
    //#endif

    @Shadow
    private boolean slim;

    @Override
    public boolean hasThinArms() {
        return slim;
    }

}
