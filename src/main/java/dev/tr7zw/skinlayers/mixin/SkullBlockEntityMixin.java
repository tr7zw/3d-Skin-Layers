package dev.tr7zw.skinlayers.mixin;

import org.spongepowered.asm.mixin.Mixin;

import dev.tr7zw.skinlayers.accessor.SkullSettings;
import dev.tr7zw.skinlayers.api.Mesh;
import net.minecraft.resources.*;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

@Mixin(SkullBlockEntity.class)
public class SkullBlockEntityMixin implements SkullSettings {

    private Mesh hatModel = null;
    private boolean initialized = false;
    private /*? >= 1.21.11 {*/ Identifier /*?} else {*/ /*ResourceLocation *//*?}*/ lastTexture = null;

    @Override
    public Mesh getHeadLayers() {
        return hatModel;
    }

    @Override
    public void setupHeadLayers(Mesh box) {
        this.hatModel = box;
    }

    @Override
    public boolean initialized() {
        return initialized;
    }

    @Override
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public void setLastTexture(/*? >= 1.21.11 {*/ Identifier /*?} else {*/ /*ResourceLocation *//*?}*/ texture) {
        this.lastTexture = texture;
    }

    @Override
    public /*? >= 1.21.11 {*/ Identifier /*?} else {*/ /*ResourceLocation *//*?}*/ getLastTexture() {
        return lastTexture;
    }

}
