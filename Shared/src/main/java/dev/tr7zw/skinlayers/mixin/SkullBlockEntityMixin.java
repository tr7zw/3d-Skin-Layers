package dev.tr7zw.skinlayers.mixin;

import org.spongepowered.asm.mixin.Mixin;

import dev.tr7zw.skinlayers.accessor.SkullSettings;
import dev.tr7zw.skinlayers.api.Mesh;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

@Mixin(SkullBlockEntity.class)
public class SkullBlockEntityMixin implements SkullSettings {

    private Mesh hatModel = null;
    
    @Override
    public Mesh getHeadLayers() {
        return hatModel;
    }

    @Override
    public void setupHeadLayers(Mesh box) {
        this.hatModel = box;
    }

}
