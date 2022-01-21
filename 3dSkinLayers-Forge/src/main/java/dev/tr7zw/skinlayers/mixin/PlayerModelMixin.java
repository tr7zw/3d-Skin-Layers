package dev.tr7zw.skinlayers.mixin;

import org.spongepowered.asm.mixin.Mixin;

import dev.tr7zw.skinlayers.accessor.PlayerEntityModelAccessor;
import net.minecraft.client.model.ModelPlayer;

@Mixin(ModelPlayer.class)
public class PlayerModelMixin implements PlayerEntityModelAccessor {

    private boolean smallArms;

    @Override
    public boolean hasThinArms() {
        return smallArms;
    }
    
}
