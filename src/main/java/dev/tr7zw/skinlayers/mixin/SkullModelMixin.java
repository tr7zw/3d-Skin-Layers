package dev.tr7zw.skinlayers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.tr7zw.skinlayers.accessor.SkullModelAccessor;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelPart;

// spotless:off 
//#if MC >= 11700
@Mixin(SkullModel.class)
//#else
//$$ import net.minecraft.client.model.HumanoidHeadModel;
//$$
//$$ @Mixin(HumanoidHeadModel.class)
//#endif
//spotless:on
public class SkullModelMixin implements SkullModelAccessor {

    @Shadow
    // spotless:off 
  //#if MC >= 11700
    private ModelPart head;
  //#else
  //$$ private ModelPart hat;
  //#endif
  //spotless:on

    @Override
    public void showHat(boolean val) {
        // spotless:off 
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
