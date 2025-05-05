package dev.tr7zw.skinlayers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.tr7zw.skinlayers.accessor.ModelPartInjector;
import dev.tr7zw.skinlayers.accessor.SkullModelAccessor;
import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.skinlayers.api.OffsetProvider;
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
    public void injectHatMesh(Mesh mesh) {
        //#if MC >= 11700
        head.getAllParts().forEach(part -> {
            if (part != head && (Object) part instanceof ModelPartInjector inj) { // is the hat, not the head
                inj.setInjectedMesh(mesh, OffsetProvider.SKULL);
            }
        });
        //#else
        //$$ ((ModelPartInjector)(Object)hat).setInjectedMesh(mesh, OffsetProvider.SKULL);
        //#endif
    }

}
