package dev.tr7zw.skinlayers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.tr7zw.skinlayers.accessor.SkullModelAccessor;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelPart;

//#if MC >= 11700
@Mixin(SkullModel.class)
//#else
//$$ import net.minecraft.client.model.HumanoidHeadModel;
//$$
//$$ @Mixin(HumanoidHeadModel.class)
//#endif
public class SkullModelMixin implements SkullModelAccessor {

    @Shadow
  //#if MC >= 11700
    private ModelPart head;
  //#else
  //$$ private ModelPart hat;
  //#endif

    @Override
    public void showHat(boolean val) {
        //#if MC >= 11700
        head.getAllParts().forEach(part -> {
            if (part != head) { // is the hat, not the head
                part.visible = val;
            }
        });
	    //#else
        //$$ hat.visible = val;
	    //#endif
	    //spotless:on

    }

}
