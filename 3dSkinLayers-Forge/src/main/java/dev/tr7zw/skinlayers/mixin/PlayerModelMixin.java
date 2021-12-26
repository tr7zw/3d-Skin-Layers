package dev.tr7zw.skinlayers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.skinlayers.accessor.PlayerEntityModelAccessor;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;

@Mixin(ModelPlayer.class)
public class PlayerModelMixin implements PlayerEntityModelAccessor {

    @Inject(method = "render", at = @At("HEAD"))
    public void render(Entity p_render_1_, float p_render_2_, float p_render_3_, float p_render_4_, float p_render_5_,
            float p_render_6_, float p_render_7_, CallbackInfo info) {
        System.out.println("render player");
    }

    private boolean smallArms;

    @Override
    public boolean hasThinArms() {
        return smallArms;
    }
    
}
