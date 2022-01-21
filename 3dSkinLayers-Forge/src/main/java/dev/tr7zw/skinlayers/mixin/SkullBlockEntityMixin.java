//package dev.tr7zw.skinlayers.mixin;
//
//import org.spongepowered.asm.mixin.Mixin;
//
//import dev.tr7zw.skinlayers.accessor.SkullSettings;
//import dev.tr7zw.skinlayers.render.CustomizableModelPart;
//import net.minecraft.tileentity.TileEntitySkull;
//
//@Mixin(TileEntitySkull.class)
//public class SkullBlockEntityMixin implements SkullSettings {
//
//    private CustomizableModelPart hatModel = null;
//    
//    @Override
//    public CustomizableModelPart getHeadLayers() {
//        return hatModel;
//    }
//
//    @Override
//    public void setupHeadLayers(CustomizableModelPart box) {
//        this.hatModel = box;
//    }
//
//}
