package dev.tr7zw.skinlayers.mixin;

import org.spongepowered.asm.mixin.Mixin;

import dev.tr7zw.skinlayers.accessor.SkullModelStateAccessor;
import dev.tr7zw.skinlayers.accessor.SkullSettings;
import lombok.Getter;
import lombok.Setter;
//#if MC >= 12109
import net.minecraft.client.model.SkullModel;

@Mixin(SkullModel.State.class)
public class SkullModelStateMixin implements SkullModelStateAccessor {

    @Getter
    @Setter
    private SkullSettings skullSettings = null;

}
//#else
//$$ @Mixin(targets = "net.minecraft.client.Minecraft") // dummy for older versions
//$$ public class SkullModelStateMixin {}
//#endif
